#!/usr/bin/env bash

set -e

set -x

PROJECT_MATT_FAT_JAR=$(ls . | grep ".jar")

java -jar ${PROJECT_MATT_FAT_JAR}