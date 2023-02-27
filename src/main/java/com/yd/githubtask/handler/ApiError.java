package com.yd.githubtask.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents an API error that occurs in a REST endpoint to be rendered as JSON.
 */
@Data
public class ApiError {

    private final int status;

    @JsonProperty("Message")
    private final String message;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
