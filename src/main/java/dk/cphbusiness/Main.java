package dk.cphbusiness;
import dk.cphbusiness.config.ApplicationConfig;
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
        // Configure the web server
        Javalin app = Javalin.create(config -> ApplicationConfig.configurations(config));
        // Configure the routes
        app.routes(Routes.getRoutes(app));
        ApplicationConfig.startServer(app);
    }
    public EndpointGroup getRoutes(Javalin app) {
        return () -> {
            app.routes(() -> {
//                path("/", authenticationRoutes.getRoutes());
                path("/", getEmployeeRoutes());
            });
//            app.exception(ApiException.class, (e, ctx) -> {
//                ctx.res().setStatus(e.getStatusCode());
//                ctx.json(new ApiException( e.getStatusCode(), e.getMessage()));
//            });
//            app.exception(NotAuthorizedException.class, (e, ctx) -> {
//                ctx.res().setStatus(e.getStatusCode());
//                ctx.json(new NotAuthorizedException(e.getStatusCode(), e.getMessage()));
//            });
            app.exception(Exception.class, (e, ctx) -> {
                ctx.res().setStatus(500);
                ctx.json(new Exception(e.getMessage()));
            });
            app.after(ctx -> {
                // TODO: Add logging
                System.out.println("After");
            });
        };
    }
    private EndpointGroup getEmployeeRoutes() {

        return () -> {
            path("/person", () -> {
                //before("/", authenticationHandler.authenticate);
                post("/", employeeHandler.createPerson);
                get("/", employeeHandler.getAllPersons);
                get("{id}", employeeHandler.getPersonById);
                put("{id}", employeeHandler.updatePersonById);
                delete("{id}", employeeHandler.deletePersonById);
            });
        };
    }
}