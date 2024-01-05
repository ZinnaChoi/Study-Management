#!/bin/bash

docker-compose down

docker volume rm mysql_MYDATA

docker-compose up -d