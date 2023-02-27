package com.yd.githubtask.external.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * External model to represent Repo.
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = Repo.Builder.class)
public final class Repo implements Serializable {

    private String name;

    private Boolean fork;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}
