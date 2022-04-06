#!/bin/bash

docker-compose stop && \
  rm -rf ./data && mkdir data && \
  docker-compose rm -f && \
  docker-compose build
  docker-compose up -d postgres rabbitmq jaeger && \
  sleep 30 && \
  docker-compose up -d restaurant order
  docker-compose logs --follow