package com.yd.githubtask.exception;

/**
 * Custom exception to be thrown when object is not found.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
