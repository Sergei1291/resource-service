package com.epam.microservices.exception;

public class Mp3ValidationException extends Exception {
    public Mp3ValidationException() {
        super();
    }

    public Mp3ValidationException(String message) {
        super(message);
    }

    public Mp3ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public Mp3ValidationException(Throwable cause) {
        super(cause);
    }
}
