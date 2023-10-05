package dk.cphbusiness.daos;

import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.entities.DepartmentEntity;
import dk.cphbusiness.entities.EmployeeEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertThrows;

class EmployeeDaoTest {
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    IDAO<EmployeeEntity> employeeDAO = EmployeeDao.getEmployeeDao(emf);
    EmployeeEntity e1, e2, e3;
    DepartmentEntity d1, d2, d3;

    @BeforeEach
    void setUp() {
        e1 = new EmployeeEntity("Hans", "Hansen", "hh@mail.com");
        e2 = new EmployeeEntity("Iver", "Issing", "ii@mail.com");
        e3 = new EmployeeEntity("Jesper", "Jensen", "jj@mail.com");
        d1 = new DepartmentEntity("IT","IT404","The guys that does the computer stuff");
        d2 = new DepartmentEntity("Sales","SA202","The sales guys");
        d3 = new DepartmentEntity("Marketing","MA101","The poster people");
        e1.setDepartmentEntity(d1);
        e2.setDepartmentEntity(d1);
        e3.setDepartmentEntity(d2);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createNamedQuery("EmployeeEntity.deleteAllRows").executeUpdate();
        em.createNamedQuery("DepartmentEntity.deleteAllRows").executeUpdate();
        em.persist(e1);
        em.persist(e2);
        em.persist(e3);
        em.persist(d1);
        em.persist(d2);
        em.persist(d3);
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test that we can create an employee")
    void create() {
        try {
            EmployeeEntity toBeCreated = new EmployeeEntity("Kurt", "Kurtson", "kk@mail.com");
            toBeCreated.setDepartmentEntity(d1);
            EmployeeEntity employee = employeeDAO.create(toBeCreated);
            assert employee.getId() != null;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    @DisplayName("Test that we can get an employee by id")
    void getById() {
        try {
            EmployeeEntity employee = employeeDAO.getById(e1.getId().toString());
            assert employee.getId() != null;
            assert employee.getDepartmentEntity() != null;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    @DisplayName("Test that we can throw an exception when getting an employee by id")
    void getByIdException() {
        assertThrows(EntityNotFoundException.class, () -> {
            EmployeeEntity employee = employeeDAO.getById("0");
        });
    }

    @Test
    @DisplayName("Test that we can get all employees")
    void getAll() {
        int expected = 3;
        List<EmployeeEntity> employees = employeeDAO.getAll();
        int actual = employees.size();
    }

    @Test
    @DisplayName("Test that we can update an employee")
    void update() {
    }

    @Test
    @DisplayName("Test that we can delete an employee")
    void delete() {
        EmployeeEntity emp = employeeDAO.delete(e1.getId().toString());
        assert emp.getLastName().equals("Hansen");
        assert employeeDAO.getAll().size() == 2;
    }

    @Test
    @DisplayName("Test that we can find an employee by property")
    void findByProperty() {
        assertThrows(UnsupportedOperationException.class, ()->employeeDAO.findByProperty("lastName", "Hansen"));
//        assert employees.size() == 1;
    }

    @Test
    @DisplayName("Test that we can validate an employee id")
    void validateId() {
        assert employeeDAO.validateId(e1.getId().toString());
    }
}