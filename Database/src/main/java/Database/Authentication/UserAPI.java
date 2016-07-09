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

import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.NoResultException;
import javax.persistence.Query;
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

    /*
    * Add a user to the system
    * */
    public boolean addUser(String username, String firstname, String lastname, String password, int organizational_id) {
        User user = new User(username, firstname, lastname, password, organizational_id);
        boolean success = false;

        if (this.checkIfUserExists(username)) {
            this.logger.severe("User already exists in database");
            return success;
        } else {
            this.session = HibernateUtilUserAuthentication.getSessionFactory().openSession();
            this.session.beginTransaction();
            this.session.save(user);
            this.session.getTransaction().commit();

            this.session.close();

            success = this.checkIfUserExists(username);
            return success;

        }


    }

    /**
     * Print the insert statement
     * @param username
     * @param firstname
     * @param lastname
     * @param password
     * @param organizational_id
     * @return
     */
    public String printMySQLInsertString(String username, String firstname, String lastname, String password, int organizational_id) {
            User user = new User(username, firstname, lastname, password, organizational_id);
            String INSERT = "INSERT INTO `CitationUserDB`.`user`(`user_id`, `first_name`,`last_name`," +
                    "`organizational_id`,`password`, `username`)"+" VALUES ("+user.getUser_id() +", "+user.getFirstName()+"," +
                    ""+user.getLastName()+", "+user.getOrganizational_id()+", "+user.getPassword()+", "+user.getUsername() +");";
            this.logger.info(INSERT);
        return INSERT;
    }

    /*
    * Check if the username exists in the database
    * * * */
    public boolean checkIfUserExists(String username) {
        User user = null;
        this.session = HibernateUtilUserAuthentication.getSessionFactory().openSession();
        this.session.beginTransaction();

        Query query = session.createQuery("from User where username = :username ");
        query.setParameter("username", username);
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException nre) {

        }



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

    /*
    * authenticate a user
    * */
    public boolean authenticateUser(String username, String password) {
        User user = null;
        boolean isPasswordCorrect = false;
        
        this.session = HibernateUtilUserAuthentication.getSessionFactory().openSession();
        this.session.beginTransaction();

        Query query = session.createQuery("from User where username = :username ");
        query.setParameter("username", username);
        user = (User) query.getSingleResult();


        this.session.close();

        if (user != null) {
            //this.logger.info("Password input: " + password + " password DB: " + user.getPassword());
            isPasswordCorrect = BCrypt.checkpw(password, user.getPassword());

            if (isPasswordCorrect) {
                this.logger.info("Password correct");

                return true;
            }

        } else {
            this.logger.severe("Username wrong or password incorrect");

        }
        return false;


    }

    /*
* Check if the username exists in the database
* * * */
    public User getUserObject(String username) {
        User user = null;
        this.session = HibernateUtilUserAuthentication.getSessionFactory().openSession();
        this.session.beginTransaction();
        Query query = session.createQuery("from User where username = :username ");
        query.setParameter("username", username);
        user = (User) query.getSingleResult();
        this.session.getTransaction().commit();
        this.session.close();

        if (user != null) {
            this.logger.info("User exists");

            return user;
        } else {
            this.logger.severe("User NOT found");
            return null;
        }
    }


}
