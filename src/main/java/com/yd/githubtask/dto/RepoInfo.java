package com.yd.githubtask.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Data Access Object to represent RepoInfo.
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = RepoInfo.Builder.class)
public final class RepoInfo implements Serializable {

    private String repoName;

    private String userName;

    private List<BranchInfo> branches;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}
