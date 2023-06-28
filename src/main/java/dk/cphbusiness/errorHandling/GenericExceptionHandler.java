package dk.cphbusiness.errorHandling;

import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import org.eclipse.jetty.http.HttpStatus;

public class GenericExceptionHandler implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Context ctx) {
        if (e instanceof ApiException) {
            ctx.status(((ApiException) e).getStatusCode());
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
