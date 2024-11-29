#!/bin/bash
mvn clean
mvn package

docker buildx build --platform linux/amd64 -t altair:latest .