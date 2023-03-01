package com.yd.githubtask.service.impl;

import com.yd.githubtask.dto.BranchInfo;
import com.yd.githubtask.dto.RepoInfo;
import com.yd.githubtask.exception.EntityNotFoundException;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link RepoServiceImpl} class.
 */
class RepoServiceImplTest {

    private static final int MOCK_WEB_SERVER_PORT = 9090;

    private static final String MOCK_BASE_URL = "http://localhost:%s".formatted(MOCK_WEB_SERVER_PORT);

    private static final String MOCK_TOCKEN = "mock_token";

    private static final String PATH_REPOS = "/users/{userName}/repos";

    private static final String PATH_BRANCHES = "/repos/{userName}/{repoName}/branches";

    private static MockWebServer mockWebServer;

    @InjectMocks
    private RepoServiceImpl sut;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(MOCK_WEB_SERVER_PORT);
    }

    @BeforeEach
    void initialize() {
        sut = new RepoServiceImpl(MOCK_BASE_URL, MOCK_TOCKEN, PATH_REPOS, PATH_BRANCHES);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void shouldGetAllReposByUsernameIncludeForksTrue() {
        final String givenUserName = "testuser";
        final Boolean givenIncludeForks = true;

        final String expectedRepoName0 = "repo0";
        final String expectedBranchName0 = "dev";
        final String expectedCommitSha0 = "1ebc4b1e471954d3a7656724dae64a328ec88a9f";

        mockWebServer.setDispatcher(prepareDispatcher());

        Flux<RepoInfo> fluxResult = sut.getRepos(givenUserName, givenIncludeForks);
        List<RepoInfo> result = fluxResult.collectList().block();

        assertThat(result).isNotNull();
        result.sort(Comparator.comparing(RepoInfo::getRepoName));
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getRepoName()).isEqualTo(expectedRepoName0);
        assertThat(result.get(0).getUserName()).isEqualTo(givenUserName);
        assertThat(result.get(0).getBranches().size()).isEqualTo(2);
        result.get(0).getBranches().sort(Comparator.comparing(BranchInfo::getBranchName));
        assertThat(result.get(0).getBranches().get(0).getBranchName()).isEqualTo(expectedBranchName0);
        assertThat(result.get(0).getBranches().get(0).getLastCommitSha()).isEqualTo(expectedCommitSha0);
    }

    @Test
    public void shouldGetNoForkReposByUsernameIncludeForksFalse() {
        final String givenUserName = "testuser";
        final Boolean givenIncludeForks = false;

        final String expectedRepoName1 = "repo1";
        final String expectedBranchName1 = "dev-123";
        final String expectedCommitSha1 = "2672c66e32f2760e64edd3f75dd28398ecf9ae48";

        mockWebServer.setDispatcher(prepareDispatcher());

        Flux<RepoInfo> fluxResult = sut.getRepos(givenUserName, givenIncludeForks);
        List<RepoInfo> result = fluxResult.collectList().block();

        assertThat(result).isNotNull();
        result.sort(Comparator.comparing(RepoInfo::getRepoName));
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(1).getRepoName()).isEqualTo(expectedRepoName1);
        assertThat(result.get(1).getUserName()).isEqualTo(givenUserName);
        assertThat(result.get(1).getBranches().size()).isEqualTo(1);
        assertThat(result.get(1).getBranches().get(0).getBranchName()).isEqualTo(expectedBranchName1);
        assertThat(result.get(1).getBranches().get(0).getLastCommitSha()).isEqualTo(expectedCommitSha1);
    }

    @Test
    public void shouldGetNoForkReposByUsernameIncludeForksNull() {
        final String givenUserName = "testuser";
        final Boolean givenIncludeForks = null;

        final String expectedRepoName1 = "repo1";
        final String expectedBranchName1 = "dev-123";
        final String expectedCommitSha1 = "2672c66e32f2760e64edd3f75dd28398ecf9ae48";

        mockWebServer.setDispatcher(prepareDispatcher());

        Flux<RepoInfo> fluxResult = sut.getRepos(givenUserName, givenIncludeForks);
        List<RepoInfo> result = fluxResult.collectList().block();

        assertThat(result).isNotNull();
        result.sort(Comparator.comparing(RepoInfo::getRepoName));
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(1).getRepoName()).isEqualTo(expectedRepoName1);
        assertThat(result.get(1).getUserName()).isEqualTo(givenUserName);
        assertThat(result.get(1).getBranches().size()).isEqualTo(1);
        assertThat(result.get(1).getBranches().get(0).getBranchName()).isEqualTo(expectedBranchName1);
        assertThat(result.get(1).getBranches().get(0).getLastCommitSha()).isEqualTo(expectedCommitSha1);
    }

    @Test
    public void shouldThrow404IfUserNotFound() {
        final String givenUserName = "nonexistent";
        final Boolean givenIncludeForks = false;
        final String expectedExceptionMessage = "GitHub user: nonexistent not found";
        mockWebServer.setDispatcher(prepareDispatcher());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> sut.getRepos(givenUserName, givenIncludeForks).collectList().block());

        Assertions.assertThat(exception).hasMessage(expectedExceptionMessage);
    }

    @Test
    public void shouldGetBranchesByUsernameAndRepoName() {
        String givenUserName = "testuser";
        String givenRepoName = "repo0";

        String expectedBranchName0 = "dev";
        String expectedSha0 = "1ebc4b1e471954d3a7656724dae64a328ec88a9f";
        String expectedBranchName1 = "master";
        String expectedSha1 = "2239dd505190703cdccc3074b4f6f92b490015f5";

        mockWebServer.setDispatcher(prepareDispatcher());

        Flux<BranchInfo> fluxResult = sut.getBranches(givenUserName, givenRepoName);
        List<BranchInfo> result = fluxResult.collectList().block();

        assertThat(result).isNotNull();
        result.sort(Comparator.comparing(BranchInfo::getBranchName));
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getBranchName()).isEqualTo(expectedBranchName0);
        assertThat(result.get(0).getLastCommitSha()).isEqualTo(expectedSha0);
        assertThat(result.get(1).getBranchName()).isEqualTo(expectedBranchName1);
        assertThat(result.get(1).getLastCommitSha()).isEqualTo(expectedSha1);
    }

    private String getResponseBodyBranchesRepo0() {
        return """
                [
                  {
                    "name": "master",
                    "commit": {"sha": "2239dd505190703cdccc3074b4f6f92b490015f5"}
                  },
                  {
                    "name": "dev",
                    "commit": {"sha": "1ebc4b1e471954d3a7656724dae64a328ec88a9f"}
                  }
                ]""";
    }

    private String getResponseBodyBranchesRepo1() {
        return """
                [
                  {
                    "name": "dev-123",
                    "commit": {"sha": "2672c66e32f2760e64edd3f75dd28398ecf9ae48"}
                  }
                ]""";
    }

    private String getResponseBodyBranchesRepo2() {
        return """
                [
                  {
                    "name": "dev-456",
                    "commit": {"sha": "af8b9f509a8cc6b9466c0dfc6c1083a24fc6aff6"}
                  }
                ]""";
    }

    private String getResponseBodyRepos() {
        return """
                [
                  {
                    "name": "repo0",
                    "fork": false
                  },
                  {
                    "name": "repo1",
                    "fork": false
                  },
                  {
                    "name": "repo2",
                    "fork": true
                  }
                ]""";
    }

    private Dispatcher prepareDispatcher() {
        return new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest request) {
                return switch (Objects.requireNonNull(request.getPath())) {
                    case "/users/testuser/repos" -> new MockResponse().setResponseCode(HttpStatus.OK.value())
                            .setBody(getResponseBodyRepos())
                            .addHeader("Content-Type", "application/json");
                    case "/repos/testuser/repo0/branches" -> new MockResponse().setResponseCode(HttpStatus.OK.value())
                            .setBody(getResponseBodyBranchesRepo0())
                            .addHeader("Content-Type", "application/json");
                    case "/repos/testuser/repo1/branches" -> new MockResponse().setResponseCode(HttpStatus.OK.value())
                            .setBody(getResponseBodyBranchesRepo1())
                            .addHeader("Content-Type", "application/json");
                    case "/repos/testuser/repo2/branches" -> new MockResponse().setResponseCode(HttpStatus.OK.value())
                            .setBody(getResponseBodyBranchesRepo2())
                            .addHeader("Content-Type", "application/json");
                    default -> new MockResponse().setResponseCode(404);
                };
            }
        };
    }
}
