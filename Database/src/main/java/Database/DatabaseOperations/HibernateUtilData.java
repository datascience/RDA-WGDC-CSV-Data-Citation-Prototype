package Database.DatabaseOperations;


import Database.Authentication.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Hibernate session management
 */
public class HibernateUtilData {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            // loads configuration and mappings
            Configuration configuration = new Configuration().configure("hibernate.data.cfg.xml");

            ServiceRegistry serviceRegistry
                    = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            // builds a session factory from the service registry
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }

        return sessionFactory;
    }
}