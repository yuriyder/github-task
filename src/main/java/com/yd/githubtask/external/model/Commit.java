package com.yd.githubtask.external.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * External model to represent Commit.
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = Commit.Builder.class)
public final class Commit implements Serializable {

    private String sha;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}
