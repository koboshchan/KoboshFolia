#!/usr/bin/env sh
set -eu
./gradlew applyAllPatches --no-configuration-cache
./gradlew applyAllServerPatches --no-configuration-cache
