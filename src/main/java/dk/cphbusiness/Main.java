package dk.cphbusiness;

import dk.cphbusiness.config.ApplicationConfig;
import dk.cphbusiness.errorHandling.GenericExceptionHandler;
import dk.cphbusiness.rest.Routes;
import io.javalin.Javalin;




public class Main {
    public static void main(String[] args) {
        // Configure the web server and setting base route to /api
        Javalin app = Javalin.create(config -> ApplicationConfig.configurations(config));
        // Configure the routes
        app.routes(Routes.getBaseRoutes());
        app.exception(Exception.class, new GenericExceptionHandler());
        app.after(ctx -> {
            // TODO: Add logging
            System.out.println("After");
        });
        ApplicationConfig.startServer(app, ApplicationConfig.getPort());
    }
    public static Javalin createServer() {
        Javalin app = Javalin.create(config -> ApplicationConfig.configurations(config));
        app.routes(Routes.getBaseRoutes());
        app.exception(Exception.class, new GenericExceptionHandler());
        app.after(ctx -> {
            System.out.println("After: Logging implemented here");
        });
        return app;
    }
    public static void stopServer(Javalin app) {
        app.stop();
    }
}