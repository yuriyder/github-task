package com.yd.githubtask.service.impl;

import com.yd.githubtask.dto.BranchInfo;
import com.yd.githubtask.dto.RepoInfo;
import com.yd.githubtask.exception.EntityNotFoundException;
import com.yd.githubtask.external.model.Branch;
import com.yd.githubtask.external.model.Repo;
import com.yd.githubtask.service.RepoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation for {@link RepoService}.
 */
@Service
public class RepoServiceImpl implements RepoService {

    private final WebClient webClient;

    private final String pathRepos;

    private final String pathBranches;

    public RepoServiceImpl(@Value("${git.base-url}") String baseUrl, @Value("${git.path.repos}") String pathRepos,
                           @Value("${git.path.branches}") String pathBranches) {
        this.webClient = WebClient.create(baseUrl);
        this.pathRepos = pathRepos;
        this.pathBranches = pathBranches;
    }

    @Override
    public Flux<RepoInfo> getRepos(String userName, Boolean isFork) {
        return webClient.get().uri(pathRepos, userName)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onRawStatus(code -> code == 404,
                        response -> Mono.error(new EntityNotFoundException("Github user: %s not found".formatted(userName))))
                .bodyToFlux(Repo.class)
                .filter(x -> isFork == null || isFork.equals(x.getFork()))
                .flatMap(repo -> getBranches(userName, repo.getName())
                        .collectList()
                        .flatMap(br -> Mono.just(RepoInfo.builder()
                                .repoName(repo.getName())
                                .userName(userName)
                                .branches(br)
                                .build())));
    }

    public Flux<BranchInfo> getBranches(String userName, String repoName) {
        return webClient.get().uri(pathBranches, userName, repoName)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Branch.class)
                .map(x -> BranchInfo.builder()
                        .branchName(x.getName())
                        .lastCommitSha(x.getCommit().getSha())
                        .build());
    }
}
