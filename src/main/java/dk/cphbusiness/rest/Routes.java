package dk.cphbusiness.rest;

import dk.cphbusiness.rest.controllers.EmployeeController;
import dk.cphbusiness.rest.controllers.SecurityController;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.security.RouteRole;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class Routes {

    public static EndpointGroup getBaseRoutes() {
        return () -> {
                path("/", getEmployeeRoutes());
                path("/", getTokenTesterRoutes());
                path("/", getSecurityRoutes());
        };
    }

    private static EndpointGroup getTokenTesterRoutes() {
        SecurityController securityHandler = SecurityController.getController();

        return () -> {
            path("/token-tester", () -> {
                before("/", securityHandler.authenticate());
                get("/", ctx -> {
                            ctx.result("Hello World");
                        },
                        Role.ANYONE);
            });
        };
    }

    private static EndpointGroup getEmployeeRoutes() {
        EmployeeController employeeHandler = EmployeeController.getHandler();
        SecurityController authenticationHandler = SecurityController.getController();

        return () -> {
            path("/employee", () -> {
                before("/", authenticationHandler.authenticate());
                post("/", employeeHandler.create(), Role.ADMIN);
                get("/", employeeHandler.getAll(), Role.ANYONE);
                get("{id}", employeeHandler.getById(), Role.USER, Role.ADMIN);
                put("{id}", employeeHandler.update(), Role.ADMIN);
                delete("{id}", employeeHandler.delete(), Role.ADMIN);
            });
        };
    }

    public static EndpointGroup getSecurityRoutes() {
        SecurityController securityController = SecurityController.getController();
        return new EndpointGroup() {
            @Override
            public void addEndpoints() {
                path("/auth", () -> {
                    post("/login", securityController.login(), Role.ANYONE);
                    post("/register", securityController.register(), Role.ANYONE);
                });
            }
        };
    }

    enum Role implements RouteRole { ANYONE, USER, ADMIN }

}
