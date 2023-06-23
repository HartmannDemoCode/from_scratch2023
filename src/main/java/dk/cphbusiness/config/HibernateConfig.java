package dk.cphbusiness.config;

import dk.cphbusiness.entities.Department;
import dk.cphbusiness.entities.Employee;
import dk.cphbusiness.utils.Utils;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.management.relation.Role;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernateConfig {
    private static EntityManagerFactory emf;
    private static EntityManagerFactory emfTest;
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = getEMF();
        }
        return emf;
    }
    public static EntityManagerFactory getEntityManagerFactoryForTest() {
        if (emfTest == null) {
            emfTest = getEMFforTest();
        }
        return emfTest;
    }

    private static EntityManagerFactory getEMF() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            boolean isDeployed = (System.getenv("DEPLOYED") != null);

            if(isDeployed) {
                String DB_PASSWORD = System.getenv("DB_PASSWORD");
                props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + getDBName());
                props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
                props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
            } else {
                props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/"+ getDBName());
                props.put("hibernate.connection.username", "dev");
                props.put("hibernate.connection.password", "ax2");
            }
            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
            // Are these needed?
            props.put("hibernate.archive.autodetection", "class");
            props.put("hibernate.current_session_context_class", "thread");
            props.put("hibernate.show_sql", "true");
            props.put("hibernate.hbm2ddl.auto", "update");

            // Hibernate Default Pool Configuration
            // https://www.mastertheboss.com/hibernate-jpa/hibernate-configuration/configure-a-connection-pool-with-hibernate/
//            props.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
//            // Maximum waiting time for a connection from the pool
//            props.put("hibernate.hikari.connectionTimeout", "10000");
//            // Minimum number of ideal connections in the pool
//            props.put("hibernate.hikari.minimumIdle", "5");
//            // Maximum number of actual connection in the pool
//            props.put("hibernate.hikari.maximumPoolSize", "20");
//            // Maximum time that a connection is allowed to sit ideal in the pool
//            props.put("hibernate.hikari.idleTimeout", "200000");

            props.put("hibernate.format_sql", "true");
            props.put("hibernate.use_sql_comments", "true");

            configuration.setProperties(props);

            getAnnotationConfiguration(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            System.out.println("Hibernate Java Config serviceRegistry created");
            SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
            EntityManagerFactory emf = sf.unwrap(EntityManagerFactory.class);
            return emf;
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static EntityManagerFactory getEMFforTest() {
        try {
            Configuration configuration = new Configuration();

            Properties props = new Properties();
            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
            props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test_db");
            props.put("hibernate.connection.username", "postgres");
            props.put("hibernate.connection.password", "postgres");
            props.put("hibernate.archive.autodetection", "class");
            props.put("hibernate.show_sql", "true");
            props.put("hibernate.hbm2ddl.auto", "create-drop");

            configuration.setProperties(props);

            getAnnotationConfiguration(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            System.out.println("Hibernate Java Config serviceRegistry created");

            return configuration.buildSessionFactory(serviceRegistry).unwrap(EntityManagerFactory.class);
        } catch (Throwable ex) {
            System.err.println("Initial EntityManagerFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void getAnnotationConfiguration(Configuration configuration) {
        configuration.addAnnotatedClass(Department.class);
        configuration.addAnnotatedClass(Employee.class);
    }

    private static String getDBName() {
        return Utils.getPomProp("db.name");
    }
}
