#!/bin/bash
#This script is useful to move the docker image to another machine by creating a tarball of the image
#if the file already exists, delete it
if [ -f altair.tar ]; then
    echo Deleting existing altair.tar
    rm altair.tar
fi

echo Making a tar file from the docker image
docker save -o altair.tar altair:latest
echo Done