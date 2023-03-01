package com.yd.githubtask.handler;

import com.yd.githubtask.exception.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
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
     * @param ex WebClientResponseException
     * @return ApiError object
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException ex) {
        logger.warn("Error from WebClient - StatusCode: %s, Message: %s".formatted(ex.getStatusCode().value(), ex.getMessage()));
        if (403 == ex.getStatusCode().value()) {
            return prepareResponse(("Exception in WebClient. Try updating GitHub Authenticating TOKEN indicated in `git.token` property"
                    + " in `application.properties` file. %s").formatted(ex.getMessage()), ex.getStatusCode());
        } else return prepareResponse("Exception in WebClient. %s".formatted(ex.getMessage()), ex.getStatusCode());
    }

    /**
     * Handle EntityNotFoundException.
     *
     * @param ex EntityNotFoundException
     * @return ApiError object
     */
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(EntityNotFoundException ex) {
        logger.info(ex.getMessage());
        return prepareResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handle HttpMediaTypeNotAcceptableException.
     *
     * @param ex            HttpMediaTypeNotAcceptableException
     * @param headers       HttpHeaders
     * @param statusCode    HttpStatusCode
     * @param request       HttpMediaTypeNotAcceptableException
     * @return ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers,
                                                                      HttpStatusCode statusCode, WebRequest request) {
        return prepareResponse("%s. %s".formatted(ex.getMessage(), ex.getBody().getDetail()), HttpStatus.NOT_ACCEPTABLE);
    }

    private ResponseEntity<Object> prepareResponse(String message, HttpStatusCode code) {
        ApiError apiError = new ApiError(code.value(), message);
        return new ResponseEntity<>(apiError, code);
    }
}
