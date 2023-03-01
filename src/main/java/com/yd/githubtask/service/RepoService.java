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
     * @param userName     GitHub username
     * @param includeForks optional flag. If 'false' or not present, only no-fork repos are returned. If 'true', all repos are returned.
     * @return Flux of {@link RepoInfo}.
     */
    Flux<RepoInfo> getRepos(String userName, Boolean includeForks);

    /**
     * Get list of branches of specified repository.
     *
     * @param userName GitHub username
     * @param repoName repository name
     * @return Flux of {@link BranchInfo}.
     */
    Flux<BranchInfo> getBranches(String userName, String repoName);
}
