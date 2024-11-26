#!/bin/bash

# Clean, package, and build the Docker image
mvn clean package docker:build

# Tag the Docker image
docker tag altair:latest marconapo/altair:latest

# Push the Docker image to the registry (optional)
docker push marconapo/altair:latest

# Bring up the services using Docker Compose
docker compose up -d