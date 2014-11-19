/*
 * Copyright [2014] [Stefan Pröll]
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

package at.stefanproell.Examples;

import at.stefanproell.PersistentIdentifierMockup.*;
import org.hibernate.Session;

/**
 * Created by stefan on 18.11.14.
 */
public class UserTest {
    private Session session;

    public static void main(String[] args) {
        System.out.println("User ");
        UserTest userTest = new UserTest();
        userTest.run();
        System.exit(0);


    }

    public void run() {
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();
        User s = new User("stefan", "1234");
        User t = new User("teresa", "5678");


        this.session.save(s);
        this.session.save(t);
        this.session.getTransaction().commit();
        this.session.close();
    }
}