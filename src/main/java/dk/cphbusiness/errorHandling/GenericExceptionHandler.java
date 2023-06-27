package dk.cphbusiness.errorHandling;

import dk.cphbusiness.errorHandling.ApiException;
import io.javalin.http.Context;
import io.javalin.util.JavalinException;
import io.javalin.http.ExceptionHandler;
import org.eclipse.jetty.http.HttpStatus;

public class GenericExceptionHandler implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Context ctx) {
        if (e instanceof ApiException) {
            ctx.status(HttpStatus.BAD_REQUEST_400);
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
        ctx.json(new ErrorResponse(e.getMessage()));
    }

    // Define a custom error response class
    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
