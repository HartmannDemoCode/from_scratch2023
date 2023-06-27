package dk.cphbusiness.daos;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.dtos.UserDTO;
import dk.cphbusiness.entities.Department;
import dk.cphbusiness.entities.Employee;
import dk.cphbusiness.errorHandling.ApiException;
import dk.cphbusiness.errorHandling.NotAuthorizedException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import dk.cphbusiness.entities.User;
import dk.cphbusiness.entities.Role;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class UserDao implements IDAO<User>, ISecurityDAO<User, Role> {
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

    public static ISecurityDAO<User, Role> getSecurityDao(EntityManagerFactory _emf) {
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
            user.getRoleList().forEach(role -> {
                if (em.find(Role.class, role.getRoleName()) == null)
                    em.persist(role);
            });
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
        if (emp == null)
            return false;
        return true;
    }

    public static void main(String[] args) throws EntityNotFoundException, Exception {
        emf = HibernateConfig.getEntityManagerFactory();
        IDAO<User> userDao = getUserDao(emf);
        ISecurityDAO<User, Role> securityDAO = getSecurityDao(emf);
//        System.out.println("DO SOME TESTING...");
        User user = new User("admin", "admin");
        user.addRole("admin");
        String token = securityDAO.createToken(user);
        System.out.println(token);
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
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.userName = :username", User.class);
        query.setParameter("username", username);
        User user = query.getSingleResult();
        if (user == null)
            throw new EntityNotFoundException("Could not find user with username: " + username);
        if (user.verifyPassword(password))
            return user;
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
    public User addUserRole(String username, String role) {
        EntityManager em = getEntityManager();
        User user = em.find(User.class, username);
        if (user == null)
            throw new EntityNotFoundException("Could not find user with username: " + username);
        Role r = em.find(Role.class, role);
        try {
            em.getTransaction().begin();
            if (r == null) {
                r = new Role(role);
                em.persist(r);
            }
            user.addRole(r.getRoleName());
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return user;
    }

    @Override
    public User removeUserRole(String username, String role) {
        EntityManager em = getEntityManager();
        User user = em.find(User.class, username);
        if (user == null)
            throw new EntityNotFoundException("Could not find user with username: " + username);
        Role r = em.find(Role.class, role);
        if (r == null)
            throw new EntityNotFoundException("Could not find role with name: " + role);
        try {
            em.getTransaction().begin();
            user.getRoleList().remove(r);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return user;
    }

    @Override
    public boolean hasRole(String role, User user) {
        return user.getRolesAsStrings().contains(role);
    }

    @Override
    public String createToken(User user) throws Exception {
        String ISSUER;
        String TOKEN_EXPIRE_TIME;
        String SECRET_KEY;

        boolean isDeployed = (System.getenv("DEPLOYED") != null);

        if (isDeployed) {
            ISSUER = System.getenv("ISSUER");
            TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
            SECRET_KEY = System.getenv("SECRET_KEY");
        } else {
            ISSUER = "cphbusiness.dk";
            TOKEN_EXPIRE_TIME = "1800000";
            SECRET_KEY = readProp("SECRET_KEY");
        }
        try {
            // https://codecurated.com/blog/introduction-to-jwt-jws-jwe-jwa-jwk/

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUserName())
                    .issuer(ISSUER)
                    .claim("username", user.getUserName())
                    .claim("roles", user.getRolesAsStrings().stream().reduce((s1, s2) -> s1 + "," + s2).get())
                    .expirationTime(new Date(new Date().getTime() + Integer.parseInt(TOKEN_EXPIRE_TIME)))
                    .build();
            Payload payload = new Payload(claimsSet.toJSONObject());

            JWSSigner signer = new MACSigner(SECRET_KEY);
            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            jwsObject.sign(signer);
            return jwsObject.serialize();

        } catch (JOSEException e) {
            e.printStackTrace();
            throw new ApiException(500, "Could not create token");
        }
    }


    @Override
    public User verifyToken(String token) throws Exception {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            boolean isDeployed = (System.getenv("DEPLOYED") != null);
            String SECRET = isDeployed ? System.getenv("SECRET_KEY") : readProp("SECRET_KEY");

            JWSVerifier verifier = new MACVerifier(SECRET);

            if (tokenSoonExpired(signedJWT)) {
                //TODO: implement renew token
            }
            if (signedJWT.verify(verifier)) {
                if (new Date().getTime() > signedJWT.getJWTClaimsSet().getExpirationTime().getTime()) {
                    throw new NotAuthorizedException(403, "Your Token is no longer valid");
                }
                return jwt2user(signedJWT);
//     return new UserPrincipal(username, roles);
            } else {
                throw new JOSEException("User could not be extracted from token");
            }
        } catch (ParseException | JOSEException e) {
            throw new NotAuthorizedException(403, "Could not validate token");
        }
    }

    private boolean tokenSoonExpired(SignedJWT signedJWT) throws ParseException, JOSEException {

//     if (new Date().getTime() > signedJWT.getJWTClaimsSet().getExpirationTime().getTime()-1000*60*5) { //If less than 5 minutes to expire
//         UserPrincipal user = jwt2user(signedJWT);
//         return utils.TokenFacade.createToken(user.getName(), user.getRoles());
//     }


        return (new Date().getTime() > signedJWT.getJWTClaimsSet().getExpirationTime().getTime() - 1000 * 60 * 5);
    }

    private User jwt2user(SignedJWT jwt) throws ParseException {
        String roles = jwt.getJWTClaimsSet().getClaim("roles").toString();
        String username = jwt.getJWTClaimsSet().getClaim("username").toString();

        Set<Role> rolesSet = Arrays
                .stream(roles.split(","))
                .map(role -> new Role(role))
                .collect(Collectors.toSet());
        return new User(username, rolesSet);
    }

    private String readProp(String propName) throws Exception {
//        try (InputStream is = new FileInputStream("/ressources/config.properties")) {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(is);
            System.out.println("PROP: " + prop.getProperty(propName) + "\n" + prop.getProperty(propName).getBytes().length);
            return prop.getProperty(propName);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new Exception("Could not read property");
        }
    }
}

