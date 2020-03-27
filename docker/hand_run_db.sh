#!/bin/bash

docker network create bravo-network

docker run --rm -e bravodb.server.self.host=bravodb1 \
  -e bravodb.server.self.port=8919 \
  --name bravodb1 \
  --network bravo-network \
  bravo/bravodb:0.1

docker run --rm -e bravodb.server.self.host=bravodb2 \
  -e bravodb.server.self.port=8919 \
  -e bravodb.server.other.host=bravodb1 \
  -e bravodb.server.other.port=8919 \
  --name bravodb2 \
  --network bravo-network \
  bravo/bravodb:0.1

docker run --rm -e bravodb.server.self.host=bravodb3 \
  -e bravodb.server.self.port=8919 \
  -e bravodb.server.other.host=bravodb2 \
  -e bravodb.server.other.port=8919 \
  --name bravodb3 \
  --network bravo-network \
  bravo/bravodb:0.1
