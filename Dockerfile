FROM registry.cn-hangzhou.aliyuncs.com/baibaicloud/baibai-platform:base

MAINTAINER baibai

ENV PARAMS=""
ENV LANG en_US.UTF-8  
ENV LANGUAGE en_US:en  
ENV LC_ALL en_US.UTF-8

RUN mkdir /app
COPY assembly-1.0.0.jar /app/assembly.jar

ENTRYPOINT ["sh","-c","java -jar /app/assembly.jar $PARAMS"]