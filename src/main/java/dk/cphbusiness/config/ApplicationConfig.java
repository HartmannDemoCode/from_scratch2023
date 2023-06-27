package dk.cphbusiness.config;

import dk.cphbusiness.dtos.UserDTO;
import dk.cphbusiness.rest.controllers.SecurityController;
import dk.cphbusiness.utils.Utils;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.plugin.bundled.RouteOverviewPlugin;
//import io.javalin.plugin.bundled.

public class ApplicationConfig {
    private static SecurityController securityController = SecurityController.getController();
    public static void configurations(JavalinConfig config) {
        // logging
        config.plugins.enableDevLogging(); // enables extensive development logging in terminal

        // http
        config.http.defaultContentType = "application/json"; // default content type for requests

        // cors
        config.accessManager((handler, ctx, permittedRoles) -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            ctx.header("Access-Control-Allow-Credentials", "true");

            if(securityController.excludeAuthentication(ctx.path())) {
                handler.handle(ctx);
            }
            else if (ctx.method().equals("OPTIONS"))
                ctx.status(200).result("OK");
           // Authorize the user based on the roles they have
            UserDTO user = ctx.attribute("user");
            if (securityController.authorize(user, permittedRoles))
                handler.handle(ctx);
            else
                ctx.status(401).result("Unauthorized");
        });

        // routing
        config.routing.contextPath = "/api"; // base path for all routes

        config.plugins.register(new RouteOverviewPlugin("/routes")); // overview of all registered routes at /routes for api documentation
    }

    public static void startServer(Javalin app) {
        app.start(getPort());
    }

    private static int getPort() {
        return Integer.parseInt(Utils.getPomProp("javalin.port"));
    }

    //    public static void stopServer(Javalin app) {
//        app.stop();
//    }

}
