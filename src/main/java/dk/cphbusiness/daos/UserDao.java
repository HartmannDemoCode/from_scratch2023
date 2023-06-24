package dk.cphbusiness.daos;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.JWTClaimsSet;
import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.entities.Department;
import dk.cphbusiness.entities.Employee;
import dk.cphbusiness.errorHandling.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import dk.cphbusiness.entities.User;
import dk.cphbusiness.entities.Role;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserDao implements IDAO<User>, ISecurityDAO<User,Role>{
    private static EntityManagerFactory emf;
    private static UserDao instance;

    //Private Constructor to ensure Singleton
    private UserDao() {
    }


    /**
     * @param _emf
     * @return an instance of this facade class.
     */
    public static IDAO<User> getUserDao(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserDao();
        }
        return instance;
    }

    public static ISecurityDAO<User,Role> getSecurityDao(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserDao();
        }
        return instance;
    }
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public User create(User user) throws Exception {
        EntityManager em = getEntityManager();
        //Look to see if a department with the provided id exists in db
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return user;
    }

    @Override
    public User getById(String id) throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        User user = em.find(User.class, id);
        if (user == null)
            throw new EntityNotFoundException("The User entity with ID: " + id + " Was not found");
        return user;
    }

    @Override
    public User delete(Long id) throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        User user = em.find(User.class, id);
        if (user == null)
            throw new EntityNotFoundException("Could not remove User with id: " + id);
        em.getTransaction().begin();
        em.remove(user);
        em.getTransaction().commit();
        return user;
    }

    @Override
    public List<User> findByProperty(String property, String propValue) throws EntityNotFoundException {
        //return null;
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean validateId(String id) {
        EntityManager em = getEntityManager();
        User emp = em.find(User.class, Long.parseLong(id));
        if(emp == null)
            return false;
        return true;
    }

    public static void main(String[] args) throws EntityNotFoundException {
        emf = HibernateConfig.getEntityManagerFactory();
        IDAO<User> userDao = getUserDao(emf);
        ISecurityDAO<User,Role> securityDAO = getSecurityDao(emf);
        System.out.println("DO SOME TESTING...");
    }

    @Override
    public List<User> getAll() {
        EntityManager em = getEntityManager();
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        List<User> users = query.getResultList();
        return users;
    }

    @Override
    public User update(User user) throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        User found = em.find(User.class, user.getUserName());
        if (found == null)
            throw new EntityNotFoundException("Could not update, User with id: " + user.getUserName() + " not found");
        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
            return user;
        } finally {
            em.close();
        }
    }

    @Override
    public User getVerifiedUser(String username, String password) {
        EntityManager em = getEntityManager();
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.userName = :username AND u.userPass = :password", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        List<User> users = query.getResultList();
        if(users.size() == 1)
            return users.get(0);
        return null;
    }

    @Override
    public Role createRole(String role) {
        EntityManager em = getEntityManager();
        Role r = new Role(role);
        try {
            em.getTransaction().begin();
            em.persist(r);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return r;
    }

    @Override
    public boolean hasRole(String role, User user) {
        return user.getRolesAsStrings().contains(role);
    }

    @Override
    public String createToken(String username, Set<String> roles) {
        String ISSUER;
        String SECRET_KEY = System.getenv("SECRET_KEY")!=null? System.getenv("SECRET_KEY"):"SOME SUPER SECRET KEY";
        String TOKEN_EXPIRE_TIME;

        boolean isDeployed = (System.getenv("DEPLOYED") != null);

        if (isDeployed) {
            ISSUER = System.getenv("ISSUER");
            TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
        } else {
            ISSUER = "cphbusiness.dk";
            TOKEN_EXPIRE_TIME = "1800000";
        }

        try {
            StringBuilder res = new StringBuilder();
            for (String string : roles) {
                res.append(string);
                res.append(",");
            }

            String rolesAsString = res.length() > 0 ? res.substring(0, res.length() - 1) : "";

            Date date = new Date();
            // https://dzone.com/articles/using-nimbus-jose-jwt-in-spring-applications-why-a
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userName)
                    .issuer(ISSUER)
                    .claim("username", userName)
                    .claim("roles", rolesAsString)
                    .expirationTime(new Date(date.getTime() + Integer.parseInt(TOKEN_EXPIRE_TIME)))
                    .build();

            Payload payload = new Payload(claims.toJSONObject());
            JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256);

            DirectEncrypter encrypter = new DirectEncrypter(SECRET_KEY.getBytes());

            JWEObject jweObject = new JWEObject(header, payload);
            jweObject.encrypt(encrypter);

            return jweObject.serialize();
        } catch (JOSEException e) {
            throw new ApiException(500, "Could not create token");
        }

    }

    @Override
    public User verifyToken(String token) {
        //return null;
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
