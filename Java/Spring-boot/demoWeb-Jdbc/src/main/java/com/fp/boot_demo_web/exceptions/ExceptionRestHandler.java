package com.fp.boot_demo_web.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Maps application exceptions to HTTP responses for REST clients.
 */
@ControllerAdvice
public class ExceptionRestHandler extends ResponseEntityExceptionHandler {

    /**
     * Maps invalid-state and invalid-argument failures to HTTP 409.
     *
     * @param ex the exception being handled
     * @param request the current web request
     * @return a conflict response
     */
    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String responseBody = "Internal conflict happened.";
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    /**
     * Maps missing-record failures to HTTP 404.
     *
     * @param ex the exception being handled
     * @param request the current web request
     * @return a not-found response
     */
    @ExceptionHandler(value = { EntityNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        String reponseBody = "Request resource not found";
        return handleExceptionInternal(ex, reponseBody, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Maps service failures to HTTP 500.
     *
     * @param ex the exception being handled
     * @param request the current web request
     * @return an internal-server-error response
     */
    @ExceptionHandler(value = { GeneralServiceException.class })
    protected ResponseEntity<Object> handleServiceError(RuntimeException ex, WebRequest request) {
        String reponseBody = "Server error";
        return handleExceptionInternal(ex, reponseBody, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
