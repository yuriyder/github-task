package com.yd.githubtask.external.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = Branch.Builder.class)
public final class Branch implements Serializable {

    private String name;

    private Commit commit;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}