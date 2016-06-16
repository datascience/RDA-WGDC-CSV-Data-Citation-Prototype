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

package at.stefanproell.PersistentIdentifierMockup;


import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

/**
 * Hibernate session management
 */
public class HibernateUtilPersistentIdentification {
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;

    static {

        Configuration configuration = null;
        try {
            configuration = new Configuration().setInterceptor(new TimeStampInterceptor()).configure("hibernate.persistentidentification.cfg.xml");


            configuration.addAnnotatedClass(at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier.class);
            configuration.addAnnotatedClass(at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlphaNumeric.class);
            configuration.addAnnotatedClass(at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlpha.class);
            configuration.addAnnotatedClass(at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierNumeric.class);
            configuration.addAnnotatedClass(at.stefanproell.PersistentIdentifierMockup.Organization.class);
            configuration.addAnnotatedClass(at.stefanproell.PersistentIdentifierMockup.TimeStampInterceptor.class);
            configuration.addAnnotatedClass(at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierSubcomponent.class);
            ServiceRegistry serviceRegistry
                    = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            // builds a session factory from the service registry
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (HibernateException e) {
            e.printStackTrace();
        }


    }


    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}