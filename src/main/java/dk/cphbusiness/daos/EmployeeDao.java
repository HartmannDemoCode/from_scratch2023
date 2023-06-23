package dk.cphbusiness.daos;

import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.entities.Department;
import dk.cphbusiness.entities.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDao implements IDAO<Employee>{
    private static EmployeeDao instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private EmployeeDao() {
    }


    /**
     * @param _emf
     * @return an instance of this facade class.
     */
    public static IDAO<Employee> getEmployeeDao(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new EmployeeDao();
        }
        return instance;
    }
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    @Override
    public Employee create(Employee employee) throws Exception {
        EntityManager em = getEntityManager();
        //Look to see if a department with the provided id exists in db
        if(employee.getDepartment() != null){
            Department dept = em.find(Department.class, employee.getDepartment().getId());
            if(dept == null)
                throw new Exception("No department with provided name found");
        }
        try {
            em.getTransaction().begin();
            em.persist(employee);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return employee;
    }

    @Override
    public Employee getById(String id) throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        Employee employee = em.find(Employee.class, id);
        if (employee == null)
            throw new EntityNotFoundException("The Employee entity with ID: " + id + " Was not found");
        return employee;
    }

    @Override
    public Employee delete(Long id) throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        Employee employee = em.find(Employee.class, id);
        if (employee == null)
            throw new EntityNotFoundException("Could not remove Employee with id: " + id);
        em.getTransaction().begin();
        em.remove(employee);
        em.getTransaction().commit();
        return employee;
    }

    @Override
    public List<Employee> findByProperty(String property, String propValue) throws EntityNotFoundException {
        //return null;
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean validateId(String id) {
        EntityManager em = getEntityManager();
        Employee emp = em.find(Employee.class, Long.parseLong(id));
        if(emp == null)
            return false;
        return true;
    }

    public static void main(String[] args) throws EntityNotFoundException {
        emf = HibernateConfig.getEntityManagerFactory();
        IDAO<Employee> dao = getEmployeeDao(emf);
        Employee employee = new Employee("Peter","Petersen","pp@mail.com");
        Employee employee2 = new Employee("Helge","Hansen","hh@mail.com");
        try {
            dao.getAll().forEach(emp->dao.delete(emp.getId()));
            dao.create(employee);
            dao.create(employee2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Employee> employees = dao.getAll();
        employees.forEach(System.out::println);
    }

    @Override
    public List<Employee> getAll() {
        EntityManager em = getEntityManager();
        TypedQuery<Employee> query = em.createQuery("SELECT p FROM Employee p", Employee.class);
        List<Employee> Employees = query.getResultList();
        return Employees;
    }

    @Override
    public Employee update(Employee employee) throws EntityNotFoundException {
        //return null;
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
