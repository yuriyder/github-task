package com.yd.githubtask.service.impl;

import com.yd.githubtask.dto.BranchInfo;
import com.yd.githubtask.dto.RepoInfo;
import com.yd.githubtask.exception.EntityNotFoundException;
import com.yd.githubtask.external.model.Branch;
import com.yd.githubtask.external.model.Repo;
import com.yd.githubtask.service.RepoService;
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

    private static final String BASE_URL = "https://api.github.com";

    private static final String API_GET_REPOSITORIES = "/users/{userName}/repos";

    private static final String API_GET_BRANCHES = "/repos/{userName}/{repoName}/branches";

    private final WebClient webClient;

    public RepoServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    @Override
    public Flux<RepoInfo> getRepos(String userName, Boolean isFork) {
        return webClient.get().uri(API_GET_REPOSITORIES, userName)
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
        return webClient.get().uri(API_GET_BRANCHES, userName, repoName)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Branch.class)
                .map(x -> BranchInfo.builder()
                        .branchName(x.getName())
                        .lastCommitSha(x.getCommit().getSha())
                        .build());
    }
}
