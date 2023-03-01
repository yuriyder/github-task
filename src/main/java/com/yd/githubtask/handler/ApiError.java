package com.yd.githubtask.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.ProblemDetail;

import java.net.URI;

/**
 * Represents an API error that occurs in a REST endpoint to be rendered as JSON.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiError extends ProblemDetail {

    private final int status;

    @JsonProperty("Message")
    private final String message;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public URI getType() {
        return null;
    }

    @Override
    public URI getInstance() {
        return null;
    }
}
