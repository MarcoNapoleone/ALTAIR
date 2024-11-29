#!/bin/bash

# This script is used to load the docker image from a tarball on another machine
echo Loading docker image
docker load -i altair.tar
echo Loaded docker image