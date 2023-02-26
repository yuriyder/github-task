# Service implements technical task.

This service provides the following endpoint to find all Github user public repositories, which are not forks, including branch names and last commits' sha:
```
GET: /repos/users/{userName}?isFork=true
```
Example request:
```
curl -X 'GET' \
  'http://localhost:8080/repos/users/testuser?isFork=true' \
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
`isFork` boolean (optional) Flag to filter repos and list only fork or no-fork ones. If this flag is absent, all repos are listed.
