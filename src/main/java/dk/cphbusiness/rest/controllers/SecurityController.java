package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.daos.IDAO;
import dk.cphbusiness.daos.ISecurityDAO;
import dk.cphbusiness.daos.UserDao;
import dk.cphbusiness.dtos.TokenDTO;
import dk.cphbusiness.dtos.UserDTO;
import dk.cphbusiness.entities.RoleEntity;
import dk.cphbusiness.entities.UserEntity;
import dk.cphbusiness.errorHandling.ApiException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecurityController implements ISecurityController{
    private static ISecurityDAO<UserEntity, RoleEntity> securityDAO;
    private static SecurityController userHandler;

    public static SecurityController getController() {
        if (securityDAO == null) {
            EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
            securityDAO = UserDao.getSecurityDao(emf);
        }
        if (userHandler == null)
            userHandler = new SecurityController();
        return userHandler;
    }

    // Private constructor to ensure Singleton
    private SecurityController() {
    }

    @Override
    public Handler login() {
        return new Handler() {
            @Override
            public void handle(Context ctx) throws ApiException {
                try {
                    UserDTO user = user = ctx.bodyAsClass(UserDTO.class);
                    UserEntity verifiedUserEntity = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());
                    if (verifiedUserEntity == null) {
                        ctx.status(401);
                        ctx.json("Wrong username or password");
                    }
                    String token = securityDAO.createToken(verifiedUserEntity);
                    ctx.status(200).json(new TokenDTO(token, user.getUsername()));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ApiException(500, "Internal server error");
                }
            }
        };
    }

    @Override
    public Handler register() {
        return new Handler() {
            @Override
            public void handle(Context context) throws Exception {
                try {
                    UserEntity userEntity = context.bodyAsClass(UserDTO.class).asEntity();
                    IDAO<UserEntity> userDao = UserDao.getUserDao(HibernateConfig.getEntityManagerFactory());
                    userEntity.addRole("UserEntity");
                    UserEntity newUserEntity = userDao.create(userEntity);
                    context.status(200).json(new UserDTO(newUserEntity));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public Handler authenticate() {
        return new Handler() {
            @Override
            public void handle(Context context) throws ApiException {
                System.out.println("Authenticating");
                try {
                    String header = context.header("Authorization");
                    if(header == null)
                        throw new ApiException(401, "No token provided");
                    String token = header.split(" ")[1];
                    if (token == null) {
                        context.status(401).json("Authorization header malformed");
                    }
                    ISecurityDAO<UserEntity, RoleEntity> securityDAO = UserDao.getSecurityDao(HibernateConfig.getEntityManagerFactory());
                    UserEntity verifiedTokenUserEntity = securityDAO.verifyToken(token);
//                    if (verifiedTokenUserEntity == null) {
//                        context.status(401).json("Invalid token");
//                    }
                    UserDTO userDTO = new UserDTO(verifiedTokenUserEntity);
                    context.attribute("user", userDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Guest user to allow access to public routes
                    context.attribute( "user", new UserDTO("user", "user", Stream.of("GUEST").collect(Collectors.toSet())));
                }
            }
        };
    }

    @Override
    public boolean authorize(UserDTO user, Set<? extends RouteRole> permittedRoles) {
        Set<String> allowedRoles = permittedRoles.stream().map(role -> role.toString()).collect(Collectors.toSet());
        if(allowedRoles.contains("ANYONE")) return true;
        AtomicBoolean hasAccess = new AtomicBoolean(false); // Since we update this in a lambda expression, we need to use an AtomicBoolean
        if (user != null) {
            user.getRoles().stream().forEach(role -> {
                if (allowedRoles.contains(role)) {
                    hasAccess.set(true);
                }
            });
        }
        return hasAccess.get();
    }
    // TODO: Not in use - remove (alternative to guest user in authenticate() method for public routes)):
    @Override
    public boolean excludeAuthentication(String path) {
        List<String> excludedPaths = List.of(
                "/api/auth/login",
                "/api/auth/register"
        );
        return excludedPaths.contains(path);
    }

}
