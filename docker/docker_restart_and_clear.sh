#!/bin/bash

docker-compose down

docker volume rm docker_MYDATA

docker volume rm docker_REDISDATA

docker-compose up -d