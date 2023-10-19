# octopus-test
Tests for Octopus project

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