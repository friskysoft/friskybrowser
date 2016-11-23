#!/bin/bash

./gradlew clean preparePhantomJS buildNodeServer startNodeServer test --rerun-tasks && \
./gradlew stopNodeServer --rerun-tasks
