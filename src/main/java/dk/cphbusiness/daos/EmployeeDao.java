package dk.cphbusiness.daos;

import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.entities.DepartmentEntity;
import dk.cphbusiness.entities.EmployeeEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class EmployeeDao implements IDAO<EmployeeEntity>{
    private static EmployeeDao instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private EmployeeDao() {
    }


    /**
     * @param _emf
     * @return an instance of this facade class.
     */
    public static IDAO<EmployeeEntity> getEmployeeDao(EntityManagerFactory _emf) {
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
    public EmployeeEntity create(EmployeeEntity employeeEntity) throws Exception {
        EntityManager em = getEntityManager();
        //Look to see if a department with the provided id exists in db
        if(employeeEntity.getDepartmentEntity() != null){
            DepartmentEntity dept = em.find(DepartmentEntity.class, employeeEntity.getDepartmentEntity().getId());
            if(dept == null)
                throw new Exception("No department with provided name found");
        }
        try {
            em.getTransaction().begin();
            em.persist(employeeEntity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return employeeEntity;
    }

    @Override
    public EmployeeEntity getById(String id) throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        EmployeeEntity employeeEntity = em.find(EmployeeEntity.class, id);
        if (employeeEntity == null)
            throw new EntityNotFoundException("The EmployeeEntity entity with ID: " + id + " Was not found");
        return employeeEntity;
    }

    @Override
    public EmployeeEntity delete(String id) throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        EmployeeEntity employeeEntity = em.find(EmployeeEntity.class, id);
        if (employeeEntity == null)
            throw new EntityNotFoundException("Could not remove EmployeeEntity with id: " + id);
        em.getTransaction().begin();
        em.remove(employeeEntity);
        em.getTransaction().commit();
        return employeeEntity;
    }

    @Override
    public List<EmployeeEntity> findByProperty(String property, String propValue) throws EntityNotFoundException {
        //return null;
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean validateId(String id) {
        EntityManager em = getEntityManager();
        EmployeeEntity emp = em.find(EmployeeEntity.class, Long.parseLong(id));
        if(emp == null)
            return false;
        return true;
    }

    public static void main(String[] args) throws EntityNotFoundException {
        emf = HibernateConfig.getEntityManagerFactory();
        IDAO<EmployeeEntity> dao = getEmployeeDao(emf);
        EmployeeEntity employeeEntity = new EmployeeEntity("Peter","Petersen","pp@mail.com");
        EmployeeEntity employeeEntity2 = new EmployeeEntity("Helge","Hansen","hh@mail.com");
        try {
            dao.getAll().forEach(emp->dao.delete(emp.getId().toString()));
            dao.create(employeeEntity);
            dao.create(employeeEntity2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EmployeeEntity> employeeEntities = dao.getAll();
        employeeEntities.forEach(System.out::println);
    }

    @Override
    public List<EmployeeEntity> getAll() {
        EntityManager em = getEntityManager();
        TypedQuery<EmployeeEntity> query = em.createQuery("SELECT p FROM EmployeeEntity p", EmployeeEntity.class);
        List<EmployeeEntity> employeeEntities = query.getResultList();
        return employeeEntities;
    }

    @Override
    public EmployeeEntity update(EmployeeEntity employeeEntity) throws EntityNotFoundException {
        //return null;
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
