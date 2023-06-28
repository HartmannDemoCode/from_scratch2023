package dk.cphbusiness.daos;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.entities.RoleEntity;
import dk.cphbusiness.entities.UserEntity;
import dk.cphbusiness.errorHandling.ApiException;
import dk.cphbusiness.errorHandling.NotAuthorizedException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class UserDao implements IDAO<UserEntity>, ISecurityDAO<UserEntity, RoleEntity> {
    private static EntityManagerFactory emf;
    private static UserDao instance;

    //Private Constructor to ensure Singleton
    private UserDao() {
    }


    /**
     * @param _emf
     * @return an instance of this facade class.
     */
    public static IDAO<UserEntity> getUserDao(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserDao();
        }
        return instance;
    }

    public static ISecurityDAO<UserEntity, RoleEntity> getSecurityDao(EntityManagerFactory _emf) {
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
    public UserEntity create(UserEntity userEntity) throws Exception {
        EntityManager em = getEntityManager();
        //Look to see if a department with the provided id exists in db
        try {
            em.getTransaction().begin();
            userEntity.getRoleEntityList().forEach(roleEntity -> {
                if (em.find(RoleEntity.class, roleEntity.getRoleName()) == null)
                    em.persist(roleEntity);
            });
            em.persist(userEntity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return userEntity;
    }

    @Override
    public UserEntity getById(String id) throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null)
            throw new EntityNotFoundException("The UserEntity entity with ID: " + id + " Was not found");
        return userEntity;
    }

    @Override
    public UserEntity delete(Long id) throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null)
            throw new EntityNotFoundException("Could not remove UserEntity with id: " + id);
        em.getTransaction().begin();
        em.remove(userEntity);
        em.getTransaction().commit();
        return userEntity;
    }

    @Override
    public List<UserEntity> findByProperty(String property, String propValue) throws EntityNotFoundException {
        //return null;
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean validateId(String id) {
        EntityManager em = getEntityManager();
        UserEntity emp = em.find(UserEntity.class, Long.parseLong(id));
        if (emp == null)
            return false;
        return true;
    }

    public static void main(String[] args) throws EntityNotFoundException, Exception {
        emf = HibernateConfig.getEntityManagerFactory();
        IDAO<UserEntity> userDao = getUserDao(emf);
        ISecurityDAO<UserEntity, RoleEntity> securityDAO = getSecurityDao(emf);
//        System.out.println("DO SOME TESTING...");
        UserEntity userEntity = new UserEntity("admin", "admin");
        userEntity.addRole("admin");
        String token = securityDAO.createToken(userEntity);
        System.out.println(token);
    }

    @Override
    public List<UserEntity> getAll() {
        EntityManager em = getEntityManager();
        TypedQuery<UserEntity> query = em.createQuery("SELECT u FROM UserEntity u", UserEntity.class);
        List<UserEntity> userEntities = query.getResultList();
        return userEntities;
    }

    @Override
    public UserEntity update(UserEntity userEntity) throws EntityNotFoundException {
        EntityManager em = getEntityManager();
        UserEntity found = em.find(UserEntity.class, userEntity.getUserName());
        if (found == null)
            throw new EntityNotFoundException("Could not update, UserEntity with id: " + userEntity.getUserName() + " not found");
        try {
            em.getTransaction().begin();
            em.merge(userEntity);
            em.getTransaction().commit();
            return userEntity;
        } finally {
            em.close();
        }
    }

    @Override
    public UserEntity getVerifiedUser(String username, String password) {
        EntityManager em = getEntityManager();
        TypedQuery<UserEntity> query = em.createQuery("SELECT u FROM UserEntity u WHERE u.userName = :username", UserEntity.class);
        query.setParameter("username", username);
        UserEntity userEntity = query.getSingleResult();
        if (userEntity == null)
            throw new EntityNotFoundException("Could not find userEntity with username: " + username);
        if (userEntity.verifyPassword(password))
            return userEntity;
        return null;
    }

    @Override
    public RoleEntity createRole(String role) {
        EntityManager em = getEntityManager();
        RoleEntity r = new RoleEntity(role);
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
    public UserEntity addUserRole(String username, String role) {
        EntityManager em = getEntityManager();
        UserEntity userEntity = em.find(UserEntity.class, username);
        if (userEntity == null)
            throw new EntityNotFoundException("Could not find userEntity with username: " + username);
        RoleEntity r = em.find(RoleEntity.class, role);
        try {
            em.getTransaction().begin();
            if (r == null) {
                r = new RoleEntity(role);
                em.persist(r);
            }
            userEntity.addRole(r.getRoleName());
            em.merge(userEntity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return userEntity;
    }

    @Override
    public UserEntity removeUserRole(String username, String role) {
        EntityManager em = getEntityManager();
        UserEntity userEntity = em.find(UserEntity.class, username);
        if (userEntity == null)
            throw new EntityNotFoundException("Could not find userEntity with username: " + username);
        RoleEntity r = em.find(RoleEntity.class, role);
        if (r == null)
            throw new EntityNotFoundException("Could not find role with name: " + role);
        try {
            em.getTransaction().begin();
            userEntity.getRoleEntityList().remove(r);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return userEntity;
    }

    @Override
    public boolean hasRole(String role, UserEntity userEntity) {
        return userEntity.getRolesAsStrings().contains(role);
    }

    @Override
    public String createToken(UserEntity userEntity) throws Exception {
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
                    .subject(userEntity.getUserName())
                    .issuer(ISSUER)
                    .claim("username", userEntity.getUserName())
                    .claim("roles", userEntity.getRolesAsStrings().stream().reduce((s1, s2) -> s1 + "," + s2).get())
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
    public UserEntity verifyToken(String token) throws Exception {
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
                throw new JOSEException("UserEntity could not be extracted from token");
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

    private UserEntity jwt2user(SignedJWT jwt) throws ParseException {
        String roles = jwt.getJWTClaimsSet().getClaim("roles").toString();
        String username = jwt.getJWTClaimsSet().getClaim("username").toString();

        Set<RoleEntity> rolesSet = Arrays
                .stream(roles.split(","))
                .map(role -> new RoleEntity(role))
                .collect(Collectors.toSet());
        return new UserEntity(username, rolesSet);
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

