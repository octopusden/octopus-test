# octopus-test
Tests for Octopus project

## GitHub Actions

- `Merge Gate` is the primary PR and `main` validation workflow. It aggregates `Quality Gates`, `Security Reports`, and all supported JVM build variants under one merge contract check: `gate/merge`.
- `Quality Gates` is reusable and manual, intended to validate style, static checks, tests, and coverage.
- `Security Reports` is reusable, scheduled, and manual, intended to publish security scan results and code scanning alerts.

## Developer View

In a regular pull request, developers should expect to see:

- `Quality Gates`
- `Security Reports`
- `Merge Gate`

`Merge Gate / gate/merge` is the external merge contract for the repository.
If it is green, all required gates passed.
If it is red, open the failed upstream check and inspect its summary, logs, or artifacts.

## DOCKER

# Start docker from a terminal

First step
```shell
docker login
```

To build a image
```shell
 docker build -t <maintainer>/<image name>:[tag] -f docker/Dockerfile .
```

To publish
```shell
docker push <maintainer>/<image name>:[tag]
```
