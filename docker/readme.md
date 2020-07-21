# 文件描述
Dockerfile 文件是用来打平台服务镜像,默认不用修改
docker-compose.yml 用来启动整个百百系统

`注意：在docker文件下有个隐藏文件 .env,docker-compose.yml 启动需要`

# .evn 文件内容
```
# mysql
MYSQL_DIR=/data/mysql
MYSQL_ROOT_PASSWORD=123456

# platform
PLATFORM_DIR=/data/platform
PLATFORM_PARAMS=--server.port=8080
```

# docker-compose.yml 使用
## 停止所有服务并删除容器
`docker-compose down`

## 创建所有百百容器并启动
`docker-compose up -d`

`注意：如果docker镜像无法下载，请使用阿里云镜像`
## 阿里云docker使用方式
```
{
  "registry-mirrors": ["https://iw3lcsa3.mirror.aliyuncs.com"]
}
```
`以上内容写入到/etc/docker/daemon.json,如果没有daemon.json文件自己创建一个`

# docker build image
docker build -t registry.cn-hangzhou.aliyuncs.com/baibaicloud/baibai-platform:1.0.0 .
