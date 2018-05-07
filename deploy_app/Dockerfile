FROM amazonlinux:latest

MAINTAINER Datafy Ventures <lekan@datafy.com.ng>

# Install aws-cli openjdk-7-jdk and wget
RUN yum -y install aws-cli java-1.8.0 wget

# Move files to necessary destinations
ADD artifacts/run-project-matt.sh /tmp/run-project-matt.sh

USER root
WORKDIR /tmp

ENTRYPOINT ["sh", "/tmp/run-project-matt.sh"]