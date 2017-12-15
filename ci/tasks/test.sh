#!/bin/bash

set -ex

pushd apps-movie-fun-code
  echo "Fetching Dependencies"
  ./mvnw springboot:run

  echo "Running Tests"
  ./mvnw test
popd

exit 0
