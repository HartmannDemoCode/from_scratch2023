package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.daos.ISecurityDAO;
import dk.cphbusiness.daos.UserDao;
import dk.cphbusiness.dtos.UserDTO;
import dk.cphbusiness.entities.Employee;
import dk.cphbusiness.entities.User;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class LoginHandler {
    public Handler login() {
        return new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                User user = ctx.bodyAsClass(UserDTO.class).asEntity();
                User securedUser = UserDao.getSecurityDao(HibernateConfig.getEntityManagerFactory())
                        .getVerifiedUser(user.getUserName(), user.getUserPass());
                ctx.json(securedUser);
            }
        };
    }
}
