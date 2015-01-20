/*
 * Copyright [2015] [Stefan Pröll]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package Database.Authentication;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.logging.Logger;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class UserAPI {
    private Logger logger;
    private Session session;


    public UserAPI() {
        this.logger = Logger.getLogger(this.getClass().getName());

    }

    public boolean addUser(String username, String password) {
        User user = new User(username, password);
        boolean success = false;
        this.session = HibernateUtilUserAuthentication.getSessionFactory().openSession();
        this.session.beginTransaction();
        this.session.save(user);
        this.session.getTransaction().commit();
        this.session.flush();
        this.session.close();

        success = this.checkIfUserExists(username);
        return success;


    }

    /*
    * Check if the username exists in the database
    * * * */
    public boolean checkIfUserExists(String username) {
        User user = null;
        this.session = HibernateUtilUserAuthentication.getSessionFactory().openSession();
        this.session.beginTransaction();
        Criteria criteria = this.session.createCriteria(User.class, "user");
        criteria.add(Restrictions.eq("user.username", username));
        user = (User) criteria.uniqueResult();
        this.session.getTransaction().commit();
        this.session.close();

        if (user != null) {
            this.logger.info("User exists");

            return true;
        } else {
            this.logger.severe("User NOT found");
            return false;
        }


    }

    public boolean authenticateUser(String username, String password) {
        User user = null;
        this.session = HibernateUtilUserAuthentication.getSessionFactory().openSession();
        this.session.beginTransaction();
        Criteria criteria = this.session.createCriteria(User.class, "user");
        criteria.add(Restrictions.eq("user.username", username));
        criteria.add(Restrictions.eq("user.password", password));
        user = (User) criteria.uniqueResult();
        this.session.getTransaction().commit();
        this.session.close();

        if (user != null) {
            this.logger.info("Password correct");

            return true;
        } else {
            this.logger.severe("Username wrong or password incorrect");
            return false;
        }


    }


}
