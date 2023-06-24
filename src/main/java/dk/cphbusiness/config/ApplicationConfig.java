package dk.cphbusiness.config;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import dk.cphbusiness.utils.Utils;
import io.javalin.plugin.bundled.RouteOverviewPlugin;

public class ApplicationConfig {

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
            handler.handle(ctx);
        });

        // routing
        config.routing.contextPath = "/api"; // base path for all routes
//        config.plugins.register(new RouteOverviewPlugin("/routes")); // overview of all registered routes at /routes for api documentation
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
