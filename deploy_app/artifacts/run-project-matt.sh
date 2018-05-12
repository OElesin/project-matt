#!/usr/bin/env bash

set -e
set -x

echo "Redis Host: " $REDIS_HOST

echo "Elasticsearch Host: " $ES_HOST

echo "S3 Bucket to scan: " $MY_S3_BUCKET

echo "<<<< Checking java version >>>>"
echo java -version

echo "<<<< Checking if JAVA_HOME is set >>>>"
echo $JAVA_HOME

aws s3 cp s3://datafy-data-lake-public-artifacts/project-matt/project-matt_1.0-BETA.jar /tmp/project-matt_1.0-BETA.jar

java -jar /tmp/project-matt_1.0-BETA.jar -Dlog4j.configurationFile=/tmp/log4j2.xml