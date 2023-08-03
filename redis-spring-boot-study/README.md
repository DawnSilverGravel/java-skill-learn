# Springboot 集成Redis

### 环境配置

- springboot 2.7
- JDK 17
- Docker Redis

```shell
mkdir -p /blessing/redis/data
mkdir -p /blessing/redis/conf
docker run -itd \
    --restart always \
    --name redis \
    -p 6379:6379 \
    -v /blessing/redis/data:/data \
    -v /blessing/redis/conf/reids.conf:/etc/redis/redis.conf \
    redis:7.0 redis-server /etc/redis/redis.conf \
    --requirepass "DawnSilverGravel" \
    --appendonly yes
```
windows版本： https://redis.io/download/

### springboot 集成redis简要介绍
