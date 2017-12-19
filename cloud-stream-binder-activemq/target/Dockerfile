FROM docker.aist.io/config-git:latest

MAINTAINER Yan Wang <610747804@qq.com>

ENV WORK_HOME=/usr/local/spring

ENV FILE_NAME=cloud-stream-binder-activemq-0.0.1-SNAPSHOT.tar.gz

RUN mkdir -p $WORK_HOME

ADD $FILE_NAME $WORK_HOME/

WORKDIR $WORK_HOME

EXPOSE 8080
CMD ["/bin/bash","start.sh"]
