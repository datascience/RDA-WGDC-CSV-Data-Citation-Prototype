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

package at.stefanproell.TomcatAuthentication;


import at.stefanproell.PersistentIdentifierMockup.HibernateUtilPersistentIdentification;
import org.hibernate.Session;

import java.util.logging.Logger;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class UserAPI {
    private Logger logger;
    private Session session;


    public UserAPI() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Resolver constructor");
    }

    public UserDetails createNewUser(String username, String password) {


        this.session = HibernateUtilPersistentIdentification.getSessionFactory().openSession();
        this.session.beginTransaction();
        UserDetails newUser = new UserDetails();
        newUser.setName(username);
        newUser.setPasswordHash(password);

        this.session.save(newUser);
        this.session.getTransaction().commit();
        this.session.flush();
        this.session.close();

        return newUser;

    }

    public GroupDetails createnewGroup(String groupname) {


        this.session = HibernateUtilPersistentIdentification.getSessionFactory().openSession();
        this.session.beginTransaction();
        GroupDetails gp = new GroupDetails("arkuser");
        this.session.save(gp);
        this.session.getTransaction().commit();
        this.session.flush();
        this.session.close();

        return gp;

    }

    public UserDetails addUserToGroup(UserDetails user, GroupDetails group) {

        this.session = HibernateUtilPersistentIdentification.getSessionFactory().openSession();
        this.session.beginTransaction();
        user.addUserToGroup(group);


        this.session.update(user);
        this.session.getTransaction().commit();
        this.session.flush();
        this.session.close();

        return user;


    }


}
