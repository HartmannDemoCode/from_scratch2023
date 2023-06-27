package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.dtos.UserDTO;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.util.Set;

public interface ISecurityController {
    Handler login();
    Handler register();
    Handler authenticate();
    boolean authorize(UserDTO user, Set<? extends RouteRole> roles);
    boolean excludeAuthentication(String path); //login and register routes should not be authenticated
}
