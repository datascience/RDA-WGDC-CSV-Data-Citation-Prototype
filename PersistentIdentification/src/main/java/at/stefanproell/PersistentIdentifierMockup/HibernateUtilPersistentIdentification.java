package at.stefanproell.PersistentIdentifierMockup;

import GenericTools.PropertyHelpers;
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
        try {


            String filename = "pid.db.properties";
            Properties prop = null;

            prop = PropertyHelpers.readPropertyFile(filename);

            String dbhost=prop.getProperty("dbhost");
            String dbport=prop.getProperty("dbport");
            String dbname=prop.getProperty("dbname");
            String dbuser=prop.getProperty("dbuser");
            String dbpw=prop.getProperty("dbpassword");

            String mysqlString = "jdbc:mysql://" + dbhost + ":"+ dbport+ "/"+ dbname;
            System.out.println("db string_ " + mysqlString);
            Properties extraProperties=new Properties();
            extraProperties.setProperty("hibernate.connection.url",mysqlString);
            extraProperties.setProperty("hibernate.connection.username",dbuser);
            extraProperties.setProperty("hibernate.connection.password",dbpw);

            Configuration configuration = new Configuration().setInterceptor(new TimeStampInterceptor());

            configuration.addProperties(extraProperties);
            configuration.configure("hibernate.persistentidentification.cfg.xml");

            serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);


        } catch (HibernateException he) {
            System.err.println("Error creating Session: " + he);
            throw new ExceptionInInitializerError(he);
        }
    }


    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}