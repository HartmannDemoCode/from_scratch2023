package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.config.HibernateConfig;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

class IControllerTest {
    private static EntityManagerFactory emf;
    private static Javalin server;

    @BeforeAll
    static void setUpAll() {
        HibernateConfig.setTestMode(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        server = Javalin.create();
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
        HibernateConfig.setTestMode(false);
        emf.close();
        server.stop();
    }

    @Test
    void getAll() {
    }

    @Test
    void getById() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}