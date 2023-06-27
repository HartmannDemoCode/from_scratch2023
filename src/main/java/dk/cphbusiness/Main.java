package dk.cphbusiness;

import dk.cphbusiness.config.ApplicationConfig;
import dk.cphbusiness.errorHandling.GenericExceptionHandler;
import dk.cphbusiness.rest.Routes;
import io.javalin.Javalin;




public class Main {
    public static void main(String[] args) {
//        Javalin app = Javalin.create().start(7080);
//        app.get("/api/demo", ctx -> {
//            ctx.result("Hello World");
//        });
        // Configure the web server and setting base route to /api
        Javalin app = Javalin.create(config -> ApplicationConfig.configurations(config));
        // Configure the routes
        app.routes(Routes.getBaseRoutes());
        app.exception(Exception.class, new GenericExceptionHandler());
        app.after(ctx -> {
            // TODO: Add logging
            System.out.println("After");
        });
        ApplicationConfig.startServer(app);
    }
//    public static EndpointGroup getRoutes(Javalin app) {
//        return () -> {
//            app.routes(() -> {
//                path("/", new AuthenticationRoute().getRoutes());
//                path("/", getEmployeeRoutes());
//                path("/", getTokenTesterRoutes());
//            });
////            app.exception(ApiException.class, (e, ctx) -> {
////                ctx.res().setStatus(e.getStatusCode());
////                ctx.json(e);
////            });
////            app.exception(NotAuthorizedException.class, (e, ctx) -> {
////                ctx.res().setStatus(e.getStatusCode());
////                ctx.json(e);
////            });
//            app.exception(Exception.class, new GenericExceptionHandler());
////                    (e, ctx) -> {
////                ctx.res().setStatus(500);
////                ctx.json(e);
////            });
//            app.after(ctx -> {
//                // TODO: Add logging
//                System.out.println("After");
//            });
//        };
//    }

//    private static EndpointGroup getTokenTesterRoutes() {
//SecurityController securityHandler = SecurityController.getHandler();
//
//        return () -> {
//            path("/token-tester", () -> {
//                before("/", securityHandler.authenticate());
//                get("/", ctx -> {
//                    ctx.result("Hello World");
//                },
//                        Role.ANYONE);
//            });
//        };
//    }
//
//    private static EndpointGroup getEmployeeRoutes() {
//        EmployeeController employeeHandler = EmployeeController.getHandler();
//        SecurityController authenticationHandler = SecurityController.getHandler();
//
//        return () -> {
//            path("/employee", () -> {
//                before("/", authenticationHandler.authenticate());
//                post("/", employeeHandler.create());
//                get("/", employeeHandler.getAll());
//                get("{id}", employeeHandler.getById());
//                put("{id}", employeeHandler.update());
//                delete("{id}", employeeHandler.delete());
//            });
//        };
//    }
//    enum Role implements RouteRole { ANYONE, USER, ADMIN }
}