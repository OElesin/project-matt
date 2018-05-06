FROM amazonlinux:latest

MAINTAINER Datafy Ventures <lekan@datafy.com.ng>

# Install aws-cli openjdk-7-jdk and wget
RUN yum -y install aws-cli openjdk-7-jdk wget

ADD artifacts/run-project-matt.sh /usr/local/bin/run-project-matt.sh
# set environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

WORKDIR /tmp