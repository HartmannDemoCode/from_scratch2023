package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.daos.IDAO;
import dk.cphbusiness.daos.ISecurityDAO;
import dk.cphbusiness.daos.UserDao;
import dk.cphbusiness.dtos.TokenDTO;
import dk.cphbusiness.dtos.UserDTO;
import dk.cphbusiness.entities.Role;
import dk.cphbusiness.entities.User;
import dk.cphbusiness.errorHandling.ApiException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController{
    private static ISecurityDAO<User, Role> securityDAO;
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
                    User verifiedUser = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());
                    if (verifiedUser == null) {
                        ctx.status(401);
                        ctx.json("Wrong username or password");
                    }
                    String token = securityDAO.createToken(verifiedUser);
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
                    User user = context.bodyAsClass(UserDTO.class).asEntity();
                    IDAO<User> userDao = UserDao.getUserDao(HibernateConfig.getEntityManagerFactory());
                    user.addRole("User");
                    User newUser = userDao.create(user);
                    context.status(200).json(new UserDTO(newUser));
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
                try {
                    String token = context.header("Authorization").split(" ")[1];
                    if (token == null) {
                        context.status(401).json("No token provided");
                    }
                    ISecurityDAO<User, Role> securityDAO = UserDao.getSecurityDao(HibernateConfig.getEntityManagerFactory());
                    User verifiedTokenUser = securityDAO.verifyToken(token);
//                    if (verifiedTokenUser == null) {
//                        context.status(401).json("Invalid token");
//                    }
                    UserDTO userDTO = new UserDTO(verifiedTokenUser);
                    context.attribute("user", userDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ApiException(401, "Invalid token");
                }
            }
        };
    }

    @Override
    public boolean authorize(UserDTO user, Set<? extends RouteRole> permittedRoles) {
        Set<String> allowedRoles = permittedRoles.stream().map(role -> role.toString()).collect(Collectors.toSet());
        if(allowedRoles.contains("ANYONE")) return true;
        AtomicBoolean hasAccess = new AtomicBoolean(false);
        if (user != null) {
            user.getRoles().stream().forEach(role -> {
                if (allowedRoles.contains(role)) {
                    hasAccess.set(true);
                }
            });
        }
        return hasAccess.get();
    }
    @Override
    public boolean excludeAuthentication(String path) {
        List<String> excludedPaths = List.of(
                "/api/auth/login",
                "/api/auth/register"
        );
        return excludedPaths.contains(path);
    }

}
