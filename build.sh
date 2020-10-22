#!/bin/bash
rm -rf assembly-1.0.0.jar
rm -rf target
mvn install
cp target/assembly-1.0.0.jar ./
docker build -t registry.cn-hangzhou.aliyuncs.com/baibaicloud/baibai-platform:1.0.6 .
