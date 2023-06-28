package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/6/16 19:20
 */
@ConfigurationProperties(prefix = "minio")
@Component
public class MinioProperties {
    private String accessKey;

    private String secretKey;

    private String endpoint;

    private String[] initBuckets;

    private String operationBucket;

    public String getOperationBucket() {
        return operationBucket;
    }

    public void setOperationBucket(String operationBucket) {
        this.operationBucket = operationBucket;
    }

    public String[] getInitBuckets() {
        return initBuckets;
    }

    public void setInitBuckets(String[] initBuckets) {
        this.initBuckets = initBuckets;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}