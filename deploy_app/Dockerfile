FROM amazonlinux:latest

MAINTAINER Olalekan Fuad Elesin <elesin.olalekan@gmail.com>

# Install aws-cli openjdk-7-jdk and wget
RUN yum -y install aws-cli java-1.8.0 wget

# Move files to necessary destinations
ADD artifacts/run-project-matt.sh /tmp/run-project-matt.sh
ADD artifacts/log4j2.xml /tmp/log4j2.xml

USER root
WORKDIR /tmp

ENTRYPOINT ["sh", "/tmp/run-project-matt.sh"]