#!/bin/bash
echo Building docker image
./build_docker_image.sh
echo Running docker compose detached
./run_docker_compose.sh