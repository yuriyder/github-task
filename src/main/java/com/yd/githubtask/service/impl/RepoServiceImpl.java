package com.yd.githubtask.service.impl;

import com.yd.githubtask.dto.BranchInfo;
import com.yd.githubtask.dto.RepoInfo;
import com.yd.githubtask.external.model.Branch;
import com.yd.githubtask.external.model.Repo;
import com.yd.githubtask.service.RepoService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RepoServiceImpl implements RepoService {

    private static final String  BASE_URL = "https://api.github.com";

    private final WebClient webClient;

    public RepoServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = WebClient.builder().baseUrl(BASE_URL).build();
    }

    @Override
    public Flux<RepoInfo> getRepos(String userName, Boolean isFork) {

        return webClient.get().uri("/users/{userName}/repos", userName)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Repo.class)
                .filter(x -> isFork == null || isFork.equals(x.getFork()))
                .flatMap(repo -> {
                    return getBranches(userName, repo.getName())
                            .collectList()
                            .flatMap(br -> {
                                return Mono.just(RepoInfo.builder()
                                        .repoName(repo.getName())
                                        .userName(userName)
                                        .branches(br)
                                        .build());
                            });
                });
    }

    public Flux<BranchInfo> getBranches(String userName, String repoName) {
        return webClient.get().uri("/repos/{userName}/{repoName}/branches", userName, repoName)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Branch.class)
                .map(x -> BranchInfo.builder()
                        .branchName(x.getName())
                        .lastCommitSha(x.getCommit().getSha())
                        .build());
    }
}
