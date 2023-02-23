package com.yd.githubtask.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = BranchInfo.Builder.class)
public final class BranchInfo implements Serializable {

    private String branchName;

    private String lastCommitSha;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}