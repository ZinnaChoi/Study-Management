#!/bin/bash

docker-compose down

docker volume rm docker_MYDATA

docker-compose up -d