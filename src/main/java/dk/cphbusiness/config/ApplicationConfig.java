package dk.cphbusiness.config;

import dk.cphbusiness.dtos.UserDTO;
import dk.cphbusiness.errorHandling.ApiException;
import dk.cphbusiness.rest.controllers.SecurityController;
import dk.cphbusiness.utils.Utils;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.plugin.bundled.RouteOverviewPlugin;
//import io.javalin.plugin.bundled.

public class ApplicationConfig {
    private static SecurityController securityController = SecurityController.getController();
    public static void configurations(JavalinConfig config) {

        config.plugins.enableDevLogging(); // enables extensive development logging in terminal
        config.http.defaultContentType = "application/json"; // default content type for requests
        config.routing.contextPath = "/api"; // base path for all routes
        config.plugins.register(new RouteOverviewPlugin("/routes")); // html overview of all registered routes at /routes for api documentation: https://javalin.io/news/2019/08/11/javalin-3.4.1-released.html

        config.accessManager((handler, ctx, permittedRoles) -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            ctx.header("Access-Control-Allow-Credentials", "true");

            if (ctx.method().equals("OPTIONS"))
                ctx.status(200).result("OK");

           // Authorize the user based on the roles they have
            UserDTO user = ctx.attribute("user");
            if (securityController.authorize(user, permittedRoles))
                handler.handle(ctx);
            else
                throw new ApiException(401, "Unauthorized");
        });

    }


    public static void startServer(Javalin app, int port) {
        app.start();
    }

    public static int getPort() {
        return Integer.parseInt(Utils.getPomProp("javalin.port"));
    }
}
