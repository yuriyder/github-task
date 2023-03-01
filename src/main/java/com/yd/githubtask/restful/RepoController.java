package com.yd.githubtask.restful;

import com.yd.githubtask.dto.RepoInfo;
import com.yd.githubtask.service.RepoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Repo controller.
 */
@RestController
@RequestMapping("/repos")
public class RepoController {

    private final RepoService repoService;

    public RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

    /**
     * Get list of github user public repositories.
     *
     * @param userName       GitHub username.
     * @param includeForks   optional flag. If 'false' or not present, only no-fork repos are returned. If 'true', all repos are returned.
     * @return List of {@link RepoInfo}.
     */
    @GetMapping(value = "/users/{userName}", produces = "application/json")
    public Flux<RepoInfo> getRepos(@PathVariable("userName") String userName,
                                   @RequestParam(value = "includeForks", required = false) Boolean includeForks) {
        return repoService.getRepos(userName, includeForks);
    }
}
