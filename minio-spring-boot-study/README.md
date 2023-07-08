# Springboot 集成 MinIO
### 环境配置
- Springboot2.7
- JDK17
- docker MinIO

### Minio服务器搭建
版本: RELEASE.2023-06-02T23-17-26Z.fips`
RedHat仓库：https://quay.io/repository/minio/minio?tab=tags
```shell
mkdir -p /blessing/minio/data
mkdir -p /blessing/minio/config
docker run -dit  \
   --restart always \
   -p 9000:9000 \
   -p 9090:9090 \
   --name minio \
   -v  /blessing/minio/data:/data \
   -v  /blessing/minio/config:/root/.minio \
   -e "MINIO_ROOT_USER=DawnSilverGravel" \
   -e "MINIO_ROOT_PASSWORD=DawnSilverGravel" \
   quay.io/minio/minio:RELEASE.2023-06-02T23-17-26Z.fips  server /data --console-address ":9090" --address ":9000"
```

### Springboot 集成MinIO简要介绍
[掘金  Springboot集成Minio](https://juejin.cn/post/7245292130625486908)