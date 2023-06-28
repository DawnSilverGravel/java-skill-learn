package com.example;

import io.minio.*;
import io.minio.messages.*;
import com.example.config.MinioProperties;
import com.example.template.MinioBucketApi;
import com.example.template.MinioObjectApi;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/6/16 19:24
 */
@SpringBootApplication
public class MinioApplication implements ApplicationRunner {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(MinioApplication.class).web(WebApplicationType.NONE).run(args);
        MinioBucketApi bean = context.getBean(MinioBucketApi.class);
        invokeMethod(bean);
//        bean.objectLockConfigurationOperation();

        MinioObjectApi objectApiBean = context.getBean(MinioObjectApi.class);
        objectApiBean.getPresignedPostFormData();
        invokeMethod(objectApiBean);
        System.out.println("结束");
//        objectApiBean.objectLegalHoldOperation();
//        SpringApplication.run(MinioApplication.class, args);
    }

    @Resource
    private MinioProperties minioProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (minioProperties.getInitBuckets().length == 0) {
            return;
        }
        // 初始化桶
        MinioClient minioClient = MinioClient.builder().credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .endpoint(minioProperties.getEndpoint())
                .build();
        // 获取所有桶
        List<Bucket> buckets = minioClient.listBuckets();
        List<String> collect = buckets.stream().map(Bucket::name).collect(Collectors.toList());
        List<String> targetBucketNames = Arrays.stream(minioProperties.getInitBuckets()).filter(s -> !collect.contains(s)).collect(Collectors.toList());
        for (String targetBucketName : targetBucketNames) {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(targetBucketName).build();
            minioClient.makeBucket(makeBucketArgs);
        }
        String operationBucket = minioProperties.getOperationBucket();
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(operationBucket).build());
        if (!bucketExists) {
            // 只能在创建时候才能设置对象锁，之后都无效
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(operationBucket).objectLock(true).build());
        }
        // 防止bucket被设置了对象锁，无法修改minio服务器的对象
        minioClient.deleteObjectLockConfiguration(DeleteObjectLockConfigurationArgs.builder().bucket(operationBucket).build());
    }


    private static void invokeMethod(Object o) {
        Method[] declaredMethods = o.getClass().getDeclaredMethods();

        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getParameters().length > 0) {
                continue;
            }
            try {

                if (declaredMethod.getReturnType().equals(void.class) && !declaredMethod.getName().contains("lambda")) {
                    declaredMethod.invoke(o);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("\n\n");
        }
    }
}