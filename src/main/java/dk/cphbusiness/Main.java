package dk.cphbusiness;
import dk.cphbusiness.config.ApplicationConfig;
import dk.cphbusiness.errorHandling.ApiException;
import dk.cphbusiness.errorHandling.NotAuthorizedException;
import dk.cphbusiness.rest.controllers.EmployeeHandler;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;


public class Main {
    public static void main(String[] args) {
//        Javalin app = Javalin.create().start(7080);
//        app.get("/api/demo", ctx -> {
//            ctx.result("Hello World");
//        });
        // Configure the web server and setting base route to /api
        Javalin app = Javalin.create(config -> ApplicationConfig.configurations(config));
        // Configure the routes
        app.routes(getRoutes(app));
        ApplicationConfig.startServer(app);
    }
    public static EndpointGroup getRoutes(Javalin app) {
        return () -> {
            app.routes(() -> {
                path("/", authenticationRoutes.getRoutes());
                path("/", getEmployeeRoutes());
            });
            app.exception(ApiException.class, (e, ctx) -> {
                ctx.res().setStatus(e.getStatusCode());
                ctx.json(e);
            });
            app.exception(NotAuthorizedException.class, (e, ctx) -> {
                ctx.res().setStatus(e.getStatusCode());
                ctx.json(e);
            });
            app.exception(Exception.class, (e, ctx) -> {
                ctx.res().setStatus(500);
                ctx.json(e);
            });
            app.after(ctx -> {
                // TODO: Add logging
                System.out.println("After");
            });
        };
    }
    private static EndpointGroup getEmployeeRoutes() {
        EmployeeHandler employeeHandler = EmployeeHandler.getHandler();

        return () -> {
            path("/employee", () -> {
                //before("/", authenticationHandler.authenticate);
                post("/", employeeHandler.create());
                get("/", employeeHandler.getAll());
                get("{id}", employeeHandler.getById());
                put("{id}", employeeHandler.update());
                delete("{id}", employeeHandler.delete());
            });
        };
    }
}