package dk.cphbusiness.rest.routes;

//import dk.cphbusiness.rest.controllers.RegisterHandler;
import dk.cphbusiness.rest.controllers.LoginHandler;
import dk.cphbusiness.rest.controllers.LoginHandler;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class AuthenticationRoute {
    private final LoginHandler loginHandler = new LoginHandler();
//    private final RegisterHandler registerHandler = new RegisterHandler();

    protected EndpointGroup getRoutes() {
        return new EndpointGroup() {
            @Override
            public void addEndpoints() {
                path("/auth", () -> {
                    post("/login", loginHandler.login());
//                post("/register", registerHandler.register);
                });
            }
        };
    }
}
