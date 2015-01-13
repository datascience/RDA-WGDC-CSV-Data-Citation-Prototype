package at.stefanproell.PersistentIdentifierMockup;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Hibernate session management
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;

    static {
        try {



            String filename = "db.properties";
            Properties prop = null;

            prop = Helpers.readPropertyFile(filename);

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
            configuration=configuration.configure("hibernate.cfg.xml");
            configuration=configuration.addProperties(extraProperties);


            configuration.configure();

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