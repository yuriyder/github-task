package com.yd.githubtask.service;

import com.yd.githubtask.dto.BranchInfo;
import com.yd.githubtask.dto.RepoInfo;
import reactor.core.publisher.Flux;

/**
 * Service for Repo.
 */
public interface RepoService {

    /**
     * Get list of github user public repositories.
     *
     * @param userName  github username
     * @param isFork    optional flag to return only fork or no-fork repos. In not present, all repos are returned.
     * @return Flux of {@link RepoInfo}.
     */
    Flux<RepoInfo> getRepos(String userName, Boolean isFork);

    /**
     * Get list of branches of specified repository.
     *
     * @param userName  github username
     * @param repoName  repository name
     * @return Flux of {@link BranchInfo}.
     */
    Flux<BranchInfo> getBranches(String userName, String repoName);
}
