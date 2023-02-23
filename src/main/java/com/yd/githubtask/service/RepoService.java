package com.yd.githubtask.service;

import com.yd.githubtask.dto.BranchInfo;
import com.yd.githubtask.dto.RepoInfo;
import reactor.core.publisher.Flux;

public interface RepoService {

    Flux<RepoInfo> getRepos(String userName, Boolean fork);

    Flux<BranchInfo> getBranches(String userName, String repoName);
}
