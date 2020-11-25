# 百百-远程控制

基于web的远程控制，无任何插件。协议支持vnc，rdp，ssh。

支持内外穿透、四层协议代理。

[官网地址http://bb.app-yun.com/](http://bb.app-yun.com/)

配合[客户端](https://github.com/baibaicloud/prober)使用即使被控PC没有公网也能进行远程控制,随时随地进行远程控制，还可支持代理局域网端口。

支持个人、企业、权限控制。

### 部署如有问题请加群讨论
![qq](https://img-blog.csdnimg.cn/20200726232850251.png)

# 系统整体技术栈
java-jdk1.8

平台-spring boot+html+css+jquery+guacamole

客户端-electron

远程控制-vnc ultra

内网穿透-frp-有进行增强，主要增加端口验证，预防恶意连接、代理。[frp源码](https://github.com/baibaicloud/frp)

数据库-mysql

平台程序支持运行在linux,windows

客户端支持windows64位、linux64。

# 开发计划

|功能名称 |开发状态|版本 |
|-----|-----|-----|
|基础功能|已支持|1.0.2|
|客户端支持windows,linux|已支持|1.0.6|
|传文件|已支持|1.2.0|
|远程控制录屏、播放录屏|开发中|1.3.0|

# 百百系统界面
### 登录
![登录.png](https://img-blog.csdnimg.cn/2020072523185896.png)

### 注册界面
![注册界面.png](https://img-blog.csdnimg.cn/20200725231914968.png)

### 网元管理
![网元管理.png](https://img-blog.csdnimg.cn/20200725231950291.png)

### 网元菜单
![网元菜单.png](https://img-blog.csdnimg.cn/2020072523200343.png)

### 远程控制
![控制.png](https://img-blog.csdnimg.cn/20200725232021807.png)

### 客户端
![控制.png](https://img-blog.csdnimg.cn/20200725232103974.png)

### 内网穿透
![内网穿透.png](https://img-blog.csdnimg.cn/20200725232124791.png)

### 企业管理
![企业管理.png](https://img-blog.csdnimg.cn/20200725232137332.png)

# 一键部署百百系统
[请查看https://github.com/baibaicloud/deployment-platform](https://github.com/baibaicloud/deployment-platform)

# 平台源码启动

使用eclipse开发工具

修改配置文件

maven 仓库地址请使用阿里云地址，也可用使用工程根目录下settings.xml

eclipse maven install

根据实际情况修改 src/main/resources/application.properties文件配置，一般修改数据库地址就可以

启动类设置为`com.loon.bridge.LauncherApplication`即可。

系统默认启动会监听`8080`端口

# 客户端源码启动
[客户端源码](https://github.com/baibaicloud/prober)

直接进入prober根目录

```powershell
npm start
```

启动之后要修改平台服务器地址

# docker build image
```
docker build -t registry.cn-hangzhou.aliyuncs.com/baibaicloud/baibai-platform:1.0.2 .
```
