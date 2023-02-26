package com.yd.githubtask.handler;

import com.yd.githubtask.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Mapping for common application exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle WebClientResponseException.
     *
     * @param ex      WebClientResponseException
     * @return        ApiError object
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException ex) {
        logger.warn("Error from WebClient - Status %s, Body %s".formatted(ex.getStatusCode().value(), ex.getResponseBodyAsString()), ex);
        return prepareResponse("WebClient Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle WebClientResponseException.
     *
     * @param ex      EntityNotFoundException
     * @return        ApiError object
     */
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(EntityNotFoundException ex) {
        logger.warn(ex.getMessage(), ex);
        return prepareResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    //TODO Need to clarify proper way of how to return custom JSON error instead of ProblemDetail object
//    @Override
//    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers,
//                                                                      HttpStatusCode statusCode, WebRequest request) {
//        logger.warn(ex.getMessage(), ex);
//        return prepareResponse(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
//    }

    private ResponseEntity<Object> prepareResponse(String message, HttpStatus code) {
        ApiError apiError = new ApiError(code.value(), message);
        return new ResponseEntity<>(apiError, code);
    }
}
