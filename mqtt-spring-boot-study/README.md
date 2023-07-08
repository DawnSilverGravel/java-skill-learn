# Springboot MQTT集成
### 环境条件
- JDK17
- Springboot 2.7 参考顶层文档
- docker
- EMQX 服务器搭建

### EMQX服务器搭建
```shell
docker run -itd \
    --restart always \
    --name emqx \
    -p 1883:1883 -p 8083:8083 \
    -p 8084:8084 -p 8883:8883 \
    -p 18083:18083 emqx/emqx:latest
```

windows：[Windows | EMQX 5.0 文档](https://www.emqx.io/docs/zh/v5.0/deploy/install-windows.html)

### Springboot集成MQTT简要介绍
[掘金](https://juejin.cn/post/7253083041609629756)
