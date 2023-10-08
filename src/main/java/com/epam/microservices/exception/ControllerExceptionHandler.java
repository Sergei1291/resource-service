package com.epam.microservices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    private final static String ERROR_MESSAGE = "errorMessage";
    private final static String ERROR_CODE = "errorCode";

    private ResponseEntity<Object> getResponseEntity(String errorMessage,
                                                     int errorCode,
                                                     HttpStatus httpStatus) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ERROR_MESSAGE, errorMessage);
        parameters.put(ERROR_CODE, errorCode);
        return new ResponseEntity<>(parameters, httpStatus);
    }

    @ExceptionHandler({Mp3ValidationException.class})
    public ResponseEntity<Object> handleException(Mp3ValidationException exception) {
        return getResponseEntity("Validation failed or request body is invalid MP3." + exception.getMessage(),
                400, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleException(ResourceNotFoundException exception) {
        return getResponseEntity("The resource with the specified id does not exist", 404, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleException(RuntimeException exception) {
        return getResponseEntity("An internal server error has occurred. " + exception.getMessage(),
                500, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
