package com.example.template;

import io.minio.*;
import io.minio.messages.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * description:
 * minio 方法测试
 *
 * @author DawnStar
 * date: 2023/6/15
 */
@Component
public class MinioBucketApi {

    @Resource
    private MinioClient minioClient;

    @Value("${minio.operation-bucket}")
    private String buckName;

    /**
     * 桶标签操作
     */
    @SneakyThrows
    public void bucketTagsOperation() {
        System.out.println("***********************桶标签操作*******************************");
        Tags tags = Tags.newBucketTags(new HashMap<String, String>(2) {{
            this.put("blessing", "star");
            this.put("images", "image");
        }});
        SetBucketTagsArgs setBucketTagsArgs = SetBucketTagsArgs.builder().bucket(buckName).tags(tags).build();
        minioClient.setBucketTags(setBucketTagsArgs);
        System.out.println("================获取桶标签========================");
        GetBucketTagsArgs getBucketTagsArgs = GetBucketTagsArgs.builder().bucket(buckName).build();
        Tags bucketTags = minioClient.getBucketTags(getBucketTagsArgs);
        System.out.println(buckName + "标签：" + bucketTags.get());

        System.out.println("================设置桶标签========================");
        SetBucketTagsArgs newSetBucketTagsArgs = SetBucketTagsArgs.builder().bucket(buckName).tags(new HashMap<String, String>(1) {{
            this.put("newTag", "newTag");
        }}).build();
        minioClient.setBucketTags(newSetBucketTagsArgs);
        Tags bucketTags1 = minioClient.getBucketTags(getBucketTagsArgs);
        System.out.println(buckName + "标签：" + bucketTags1.get());

        System.out.println("================删除桶标签=======================");
        DeleteBucketTagsArgs deleteBucketTagsArgs = DeleteBucketTagsArgs.builder().bucket(buckName).build();
        minioClient.deleteBucketTags(deleteBucketTagsArgs);
        Tags newTags = minioClient.getBucketTags(getBucketTagsArgs);
        System.out.println(buckName + "标签：" + newTags.get());
        System.out.println("***********************************************************************************");

    }


    /**
     * 桶版本操作
     */
    @SneakyThrows
    public void bucketVersionOperation() {
        System.out.println("***********************桶版本操作*******************************");
        System.err.println("桶版本默认没有，开启了就只有两种状态：Enable 和 Suspended状态");
        System.out.println("=================获取桶版本===================");
        GetBucketVersioningArgs getBucketVersioningArgs = GetBucketVersioningArgs.builder().bucket(buckName).build();
        VersioningConfiguration bucketVersioning = minioClient.getBucketVersioning(getBucketVersioningArgs);
        System.out.println(buckName + "版本：" + bucketVersioning.status().toString() + bucketVersioning.isMfaDeleteEnabled());

        System.out.println("===================设置桶版本==================");
        VersioningConfiguration.Status enabled = VersioningConfiguration.Status.ENABLED;
        VersioningConfiguration configuration = new VersioningConfiguration(enabled, true);
        SetBucketVersioningArgs setBucketVersioningArgs = SetBucketVersioningArgs.builder().bucket(buckName).config(configuration).build();
        minioClient.setBucketVersioning(setBucketVersioningArgs);
        bucketVersioning = minioClient.getBucketVersioning(getBucketVersioningArgs);
        System.out.println(buckName + "版本：" + bucketVersioning.status().toString() + bucketVersioning.isMfaDeleteEnabled());
        System.out.println("***********************************************************************************");

    }

    /**
     * 桶基本操作
     */
    @SneakyThrows
    public void bucketBasicOperation() {
        System.out.println("***********************桶基本操作*******************************");
        System.out.println("===============检查桶是否存在========================");
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(buckName).build();
        boolean bucketExists = minioClient.bucketExists(bucketExistsArgs);
        System.out.println(buckName + "存在：" + bucketExists);
        // 获取桶列表
        System.out.println("=================获取桶列表======================");
        List<Bucket> buckets = minioClient.listBuckets();
        buckets.forEach(bucket -> System.out.println(bucket.name()));

        System.out.println("=================新建桶============================");
        MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket("test").objectLock(true).build();
        // 创建已有bucket会创建失败
        minioClient.makeBucket(makeBucketArgs);
        buckets = minioClient.listBuckets();
        buckets.forEach(bucket -> System.out.println(bucket.name()));


        // 删除桶,有文件就会删除失败
        System.out.println("=================删除桶======================");
        RemoveBucketArgs removeBucketArgs = RemoveBucketArgs.builder().bucket("test").build();
        minioClient.removeBucket(removeBucketArgs);
        buckets = minioClient.listBuckets();
        buckets.forEach(bucket -> System.out.println(bucket.name()));
        System.out.println("***********************************************************************************");

    }


    @SneakyThrows
    public void objectLockConfigurationOperation() {
        System.out.println("***********************桶对象锁配置*******************************");

        GetObjectLockConfigurationArgs getObjectLockConfigurationArgs = GetObjectLockConfigurationArgs.builder().bucket(buckName).build();
        ObjectLockConfiguration objectLockConfiguration = minioClient.getObjectLockConfiguration(getObjectLockConfigurationArgs);
        System.out.println("获取当前的bucket对象锁配置"+objectLockConfiguration.mode() + " " + objectLockConfiguration.duration());


        objectLockConfiguration = new ObjectLockConfiguration(RetentionMode.COMPLIANCE, new RetentionDurationDays(1));
        minioClient.setObjectLockConfiguration(SetObjectLockConfigurationArgs.builder().bucket(buckName).config(objectLockConfiguration).build());
        objectLockConfiguration = minioClient.getObjectLockConfiguration(getObjectLockConfigurationArgs);
        System.out.println("设置后的bucket对象锁配置"+objectLockConfiguration.mode() + " " + objectLockConfiguration.duration());


        minioClient.deleteObjectLockConfiguration(DeleteObjectLockConfigurationArgs.builder().bucket(buckName).build());
        objectLockConfiguration = minioClient.getObjectLockConfiguration(getObjectLockConfigurationArgs);
        System.out.println("删除桶锁对象配置："+objectLockConfiguration.mode() + " " + objectLockConfiguration.duration());
        System.out.println("***********************************************************************************");

    }

}