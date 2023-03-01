# Service implements technical task.

This service provides the following endpoint to find all Github user public repositories, which are not forks, including branch names and last commits' sha:
```
GET: /repos/users/{userName}?includeForks=false
```
Example request:
```
curl -X 'GET' \
  'http://localhost:8080/repos/users/testuser?includeForks=false' \
  -H 'Accept: application/json'
```
Response schema:
```
[
  {
    "repoName": "string",
    "userName": "string",
    "branches": [
      {
        "branchName": "string",
        "lastCommitSha": "string"
      }
    ]
  }
]
```
Headers:
`Accept` setting to `application/json`
Path parameters:
`username` string. GitHub user account.
Query parameters:
`includeForks` boolean (optional) flag to include fork repos in result. If this flag is absent or 'false', only no-fork repositories are returned.

In case of frequent calls to GitHub API, rate limiter restrictions may be applied by GitHub. In this case it is necessary to update Authenticating TOKEN indicated in `git.token` property in `application.properties` file. 