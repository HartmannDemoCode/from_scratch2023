package dk.cphbusiness.errorHandling;

import lombok.Getter;

public class ApiException extends Exception{
        @Getter
        private final int statusCode;
        @Getter
        private final String message;

        public ApiException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
            this.message = message;
        }

        public ApiException(int statusCode, String message, Throwable cause) {
            super(message, cause);
            this.statusCode = statusCode;
            this.message = message;
        }
}
