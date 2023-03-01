package com.yd.githubtask.restful;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yd.githubtask.dto.RepoInfo;
import com.yd.githubtask.handler.ApiError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

/**
 * Integration Tests for {@link RepoController} class.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10000")
class RepoControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void shouldGetNoForkRepos() throws Exception {
        String givenUserName = "octocat";

        List<String> expectedRepoNames = List.of("git-consortium", "hello-worId", "test-repo1", "Hello-World", "octocat.github.io", "Spoon-Knife");
        int expectedBranchesNum = 11;

        EntityExchangeResult<String> result = webTestClient.get().uri("/repos/users/%s".formatted(givenUserName))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .returnResult();
        List<RepoInfo> resultRepoInfoList = objectMapper.readValue(result.getResponseBody(), new TypeReference<>() {});

        Assertions.assertEquals(expectedRepoNames.size(), resultRepoInfoList.size());
        Assertions.assertTrue(resultRepoInfoList.stream().map(RepoInfo::getRepoName).toList().containsAll(expectedRepoNames));
        Assertions.assertEquals(expectedBranchesNum, resultRepoInfoList.stream().mapToLong(x -> x.getBranches().size()).sum());
    }

    @Test
    public void shouldGetAllRepos() throws Exception {
        String givenUserName = "octocat";

        List<String> expectedRepoNames = List.of("git-consortium", "hello-worId", "test-repo1", "Hello-World", "octocat.github.io",
                "Spoon-Knife", "boysenberry-repo-1", "linguist");
        int expectedBranchesNum = 26;


        EntityExchangeResult<String> result = webTestClient.get().uri("/repos/users/%s?includeForks=true".formatted(givenUserName))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .returnResult();
        List<RepoInfo> resultRepoInfoList = objectMapper.readValue(result.getResponseBody(), new TypeReference<>() {});

        Assertions.assertEquals(expectedRepoNames.size(), resultRepoInfoList.size());
        Assertions.assertTrue(resultRepoInfoList.stream().map(RepoInfo::getRepoName).toList().containsAll(expectedRepoNames));
        Assertions.assertEquals(expectedBranchesNum, resultRepoInfoList.stream().mapToLong(x -> x.getBranches().size()).sum());
    }

    @Test
    public void shouldGet406ErrorForApplicationXMLHeader() throws Exception {
        String givenUserName = "octocat";

        String expectedApiErrorMessage = "No acceptable representation. Acceptable representations: [application/json].";
        int expectedApiErrorStatus = 406;

        EntityExchangeResult<String> result = webTestClient.get().uri("/repos/users/%s".formatted(givenUserName))
                .accept(MediaType.APPLICATION_XML)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE)
                .expectBody(String.class)
                .returnResult();

        ApiError resultError = objectMapper.readValue(result.getResponseBody(), ApiError.class);
        Assertions.assertEquals(expectedApiErrorStatus, resultError.getStatus());
        Assertions.assertEquals(expectedApiErrorMessage, resultError.getMessage());
    }
}
