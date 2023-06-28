package com.example.template;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.example.util.MinioFileUtils;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.*;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author DawnStar
 * date: 2023/6/16
 */
@Component
public class MinioObjectApi {
    @Resource
    private MinioClient minioClient;

    @Value("${minio.operation-bucket}")
    private String buckName;

    @SneakyThrows
    public void putObject() {
        // 上传已知的大小的文件
        System.out.println("************************************putObject操作***********************************************");
        String fileName = "test.txt";
        InputStream inputStream = MinioFileUtils.createFile(fileName);
        System.out.println("==================上传已知的大小的文件==========================");
        PutObjectArgs firstObjectArgs = PutObjectArgs.builder()
                .bucket(buckName)
                .object(fileName)
                // -1 表示默认分片为 5M
                .stream(inputStream, inputStream.available(), -1).build();
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(firstObjectArgs);
        System.out.println(objectWriteResponse.versionId() + " " + objectWriteResponse.etag());

        // 上传未知大小的文件
        System.out.println("==================上传未知的大小的文件==========================");
        PutObjectArgs secondObjectArgs = PutObjectArgs.builder()
                .bucket(buckName)
                .object(fileName)
                .stream(inputStream, -1, PutObjectArgs.MIN_MULTIPART_SIZE)
                .build();
        objectWriteResponse = minioClient.putObject(secondObjectArgs);
        System.out.println(objectWriteResponse.versionId() + " " + objectWriteResponse.etag());

        // 上传未知大小的文件
        System.out.println("==================上传内存中的数据==========================");
        String content = "这是一个内存运行的数据";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Amz-Storage-Class", "REDUCED_REDUNDANCY");
        Map<String, String> userMetadata = new HashMap<>();
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contentBytes);
        PutObjectArgs thirdObjectArgs = PutObjectArgs.builder()
                .bucket(buckName)
                // 将创建一个名为putMethod文件夹
                .object("putMethod/content.txt")
                // 两者不能同时为-1
                .stream(byteArrayInputStream, -1, PutObjectArgs.MIN_MULTIPART_SIZE)
                .headers(headers)
                .userMetadata(userMetadata)
                .build();
        objectWriteResponse = minioClient.putObject(thirdObjectArgs);
        System.out.println(objectWriteResponse.versionId() + " " + objectWriteResponse.etag());
        System.out.println("***********************************************************************************");

    }

    @SneakyThrows
    public void uploadObject() {
        System.out.println("************************************uploadObject操作***********************************************");

        String fileName = "uploadObject.txt";
        File file = MinioFileUtils.getUploadFile(fileName);
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket(buckName)
                .object(fileName)
//                .filename(file.toPath())
                .filename(file.toPath().toString(), UploadObjectArgs.MAX_PART_SIZE)
                .build();
        ObjectWriteResponse objectWriteResponse = minioClient.uploadObject(uploadObjectArgs);
        System.out.println(objectWriteResponse.versionId() + " " + objectWriteResponse.etag());
        System.out.println("***********************************************************************************");
    }

    @SneakyThrows
    public void copyObject() {
        String fileName = "copy.txt";
        putObjectByPartSize("", fileName);
        System.out.println("************************************copyObject操作***********************************************");
        // object() 与 source()不可相同
        CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder()
                .bucket(buckName)
                .object("copyMethod/" + fileName)
                .source(CopySource.builder().bucket(buckName).object(fileName).build())
                .build();
        ObjectWriteResponse objectWriteResponse = minioClient.copyObject(copyObjectArgs);
        System.out.println(objectWriteResponse.versionId() + " " + objectWriteResponse.etag());
        System.out.println("***********************************************************************************");
    }

    @SneakyThrows
    public void composeObject() {
        System.out.println("************************************composeObject操作***********************************************");
        List<ComposeSource> composeSources = new ArrayList<>();
        String prefix = "subComposeObject";
        for (int i = 0; i < 2; i++) {
            String composeFileName = prefix + i + ".txt";
            putObjectByPartSize("composeMethod", composeFileName);
            ComposeSource composeSource = ComposeSource.builder()
                    .bucket(buckName)
                    .object("composeMethod/" + composeFileName)
                    .build();
            composeSources.add(composeSource);
        }
        String fileName = "composeObject.txt";
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket(buckName)
                .object("composeMethod/" + fileName)
                // 每个文件大小不可以低于5M
                .sources(composeSources)
                .build();
        ObjectWriteResponse objectWriteResponse = minioClient.composeObject(composeObjectArgs);
        System.out.println(objectWriteResponse.versionId() + " " + objectWriteResponse.etag());
        System.out.println("***********************************************************************************");

    }

    @SneakyThrows
    public void uploadSnowballObjects() {
        System.out.println("************************************uploadSnowballObjects操作***********************************************");
        String prefix = "uploadSnowballMethod";
        List<SnowballObject> objects = new ArrayList<>();
        // 参数中size必须与文件大小相等
        String content = MinioFileUtils.generateContent(prefix + "my-object-one");
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        InputStream inputStream = MinioFileUtils.createFile(prefix + "my-object-two");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contentBytes);
        objects.add(
                new SnowballObject(
                        prefix + "/my-object-one",
                        byteArrayInputStream,
                        byteArrayInputStream.available(),
                        null));
        objects.add(
                new SnowballObject(
                        prefix + "/my-object-two",
                        inputStream,
                        inputStream.available(),
                        null));
        ObjectWriteResponse objectWriteResponse = minioClient.uploadSnowballObjects(
                UploadSnowballObjectsArgs.builder().bucket(buckName).objects(objects).build());
        System.out.println(objectWriteResponse.versionId() + " " + objectWriteResponse.etag());
        System.out.println("***********************************************************************************");
    }

    @SneakyThrows
    public void removeObject() {
        System.out.println("************************************removeObject与removeObjects操作***********************************************");
        System.out.println("====================删除单个对象================================");
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(buckName).object("test").build();

        // 删除不存在的对象也不会有问题
        minioClient.removeObject(removeObjectArgs);
        DeleteObject deleteObject = new DeleteObject("test");

        System.out.println("====================删除多个对象================================");
        String prefix = "removeMethod";
        DeleteObject deleteObject1 = new DeleteObject(prefix + "/removeObject.txt");
        putObjectByPartSize(prefix, "removeObject.txt");
        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket(buckName).objects(new ArrayList<>() {{
            this.add(deleteObject);
            this.add(deleteObject1);
        }}).build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        // 删除不存在的对象也不会有问题
        for (Result<DeleteError> deleteErrorResult : results) {
            System.out.println(deleteErrorResult.get().toString());
        }
        System.out.println("***********************************************************************************");
    }


    @SneakyThrows
    public void statObject() {
        System.out.println("************************************statObjects操作***********************************************");
        uploadObject("statMethod", "statObject.txt");
        StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                .bucket(buckName)
                .object("statMethod/statObject.txt")
                .build();
        StatObjectResponse statObjectResponse = minioClient.statObject(statObjectArgs);
        System.out.println("statObject：" + statObjectResponse.etag() + " " + statObjectResponse.object() + " " + statObjectResponse.versionId());
        System.out.println("***********************************************************************************");
    }


    @SneakyThrows
    public void getObject() {
        System.out.println("************************************getObjects操作***********************************************");
        uploadObject("getMethod", "getObject.txt");
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(buckName)
                .object("getMethod/getObject.txt")
                // 起始位置
                .offset(10L)
                // 数据长度
                .length(100L)
                .build();
        GetObjectResponse response = minioClient.getObject(getObjectArgs);
        byte[] bytes = response.readAllBytes();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bytes.length);
        System.out.println(byteArrayOutputStream);
        byteArrayOutputStream.close();
        response.close();
        System.out.println("***********************************************************************************");


    }


    @SneakyThrows
    public void getPresignedObjectUrl() {
        System.out.println("************************************getPresignedObjectUrl操作***********************************************");

        GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(buckName)
                .object("getPresignedMethod/notGetPresignedObjectUrl.txt")
                .expiry(24, TimeUnit.HOURS)
                .method(Method.GET)
                .build();
        String presignedObjectUrl = minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        System.out.println("没有该文件的路径" + presignedObjectUrl);


        // 获取上传路径
        GetPresignedObjectUrlArgs putPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(buckName)
                .object("getPresignedMethod/getPresignedObjectUrl.txt")
                .expiry(24, TimeUnit.HOURS)
                .method(Method.PUT)
                .build();
        presignedObjectUrl = minioClient.getPresignedObjectUrl(putPresignedObjectUrlArgs);
        System.out.println("PUT方法上传路径：" + presignedObjectUrl);

        // 上传数据
        HttpRequest request = HttpUtil.createRequest(cn.hutool.http.Method.PUT, presignedObjectUrl);
        byte[] bytes = MinioFileUtils.generateContent("getPresignedObjectUrl.txt").getBytes(StandardCharsets.UTF_8);
        HttpResponse execute = request.body(bytes).contentType(ContentType.TEXT_PLAIN.getValue()).execute();
        execute.close();


        GetPresignedObjectUrlArgs targetPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(buckName)
                .object("getPresignedMethod/getPresignedObjectUrl.txt")
                .expiry(24, TimeUnit.HOURS)
                .method(Method.GET)
                .build();
        presignedObjectUrl = minioClient.getPresignedObjectUrl(targetPresignedObjectUrlArgs);
        System.out.println("获取路径：" + presignedObjectUrl);


        GetPresignedObjectUrlArgs headPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(buckName)
                .object("getPresignedMethod/getPresignedObjectUrl.txt")
                .method(Method.HEAD)
                .build();
        presignedObjectUrl = minioClient.getPresignedObjectUrl(headPresignedObjectUrlArgs);
        System.out.println("HEAD方法路径，请通过postman请求该路径：" + presignedObjectUrl);
        System.out.println("***********************************************************************************");

    }


    @SneakyThrows
    public void downLoadObject() {
        System.out.println("************************************downLoadObject操作***********************************************");
        uploadObject("downloadMethod", "downloadObject.txt");
        DownloadObjectArgs downloadObjectArgs = DownloadObjectArgs.builder()
                .bucket(buckName)
                .object("downloadMethod/downloadObject.txt")
                .filename("downloadObject.txt")
                .build();
        // 文件名存在将会下载失败
        System.err.println("文件名存在将会下载失败");
        minioClient.downloadObject(downloadObjectArgs);
        File file = new File("downloadObject.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String readLine;
        while ((readLine = bufferedReader.readLine()) != null) {
            System.out.print(readLine);
        }
        bufferedReader.close();
        System.out.println("***********************************************************************************");
    }

    @Value("${minio.endpoint}")
    private String url;

    @SneakyThrows
    public void getPresignedPostFormData() {
        System.out.println("************************************getPresignedPostFormData操作***********************************************");

        // 创建一个时效为7天的策略
        PostPolicy policy = new PostPolicy(buckName, ZonedDateTime.now().plusDays(7));
        String objectName = "PostFormMethod/postFormData.txt";
        // 添加条件，`key`键为对象名称
        policy.addEqualsCondition("key", objectName);

        // 添加'Content-Type.设置前缀为“application”
        policy.addStartsWithCondition("Content-Type", "application/");

        // 添加内容长度范围，20Bit - 1MiB，不在这个区间的文件上传失败
        policy.addContentLengthRangeCondition(20, 1024*1024);

        Map<String, String> formData = minioClient.getPresignedPostFormData(policy);
        // 以表单的形式上传文本
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
        multipartBuilder.setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        multipartBuilder.addFormDataPart("key", objectName);
        multipartBuilder.addFormDataPart("Content-Type",ContentType.OCTET_STREAM.getValue());
        String fileName = objectName.substring(objectName.lastIndexOf("/") + 1);
        File uploadFile = MinioFileUtils.getUploadFile(fileName);
        // "file" 必须最后添加
        multipartBuilder.addFormDataPart(
                "file", objectName, RequestBody.create(uploadFile, null));
        System.out.println(url+"/"+objectName);
        Request request =
                new Request.Builder()
                        // minio服务器路径
                        .url(url +"/"+buckName)
                        .post(multipartBuilder.build())
                        .build();
        OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
        try (Response response = httpClient.newCall(request).execute();) {
            if (response.isSuccessful()) {
                System.out.println(fileName + " is uploaded successfully using POST object");
            } else {
                System.out.println("Failed to upload " + fileName);
            }
        }
        System.out.println("***********************************************************************************");
    }


    @SneakyThrows
    public void listObjects() {
        System.out.println("************************************listObjects操作***********************************************");
        uploadObject("listMethod", "listObject1.txt");
        uploadObject("listMethod", "listObject2.txt");
        uploadObject("listMethod", "listObject3.txt");
        System.out.println("===================第一种List==================");
        ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder().bucket(buckName).build();
        Iterable<Result<Item>> listObjects = minioClient.listObjects(listObjectsArgs);
        print(listObjects);

        System.out.println("===================第二种List==================");
        listObjectsArgs = ListObjectsArgs.builder()
                .bucket(buckName)
                .recursive(true)
                .build();
        print(minioClient.listObjects(listObjectsArgs));

        System.out.println("===================第三种List==================");
        listObjectsArgs = ListObjectsArgs.builder()
                .bucket(buckName)
                .recursive(true)
                .prefix("listMethod")
                .build();
        print(minioClient.listObjects(listObjectsArgs));

        System.out.println("===================第四种List==================");
        listObjectsArgs = ListObjectsArgs.builder()
                .bucket(buckName)
                .recursive(true)
                .prefix("listMethod")
                .startAfter("listMethod/listObject2.txt")
                .build();
        print(minioClient.listObjects(listObjectsArgs));
        // ......
        System.out.println("***********************************************************************************");
    }

    private void print(Iterable<Result<Item>> listObjects) throws Exception {
        for (Result<Item> listObject : listObjects) {
            System.out.println(listObject.get().objectName());
        }
    }


    @SneakyThrows
    public void objectRetentionOperation() {
        System.out.println("************************************对象保留策略操作***********************************************");
        System.err.println("创建bucket需要设置objectLock为true");
        uploadObject("retentionMethod", "objectRetention.txt");
        GetObjectRetentionArgs getObjectRetentionArgs = GetObjectRetentionArgs.builder().bucket(buckName).object("retentionMethod/objectRetention.txt")
                .build();
        Retention objectRetention = minioClient.getObjectRetention(getObjectRetentionArgs);
        if (objectRetention == null) {
            System.out.println("修改策略前配置: null");
        } else {
            System.out.println("修改策略前配置: " + objectRetention.mode().toString() + " " + objectRetention.retainUntilDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        Retention retention = new Retention(RetentionMode.COMPLIANCE, ZonedDateTime.now().plusSeconds(40));
        SetObjectRetentionArgs setObjectRetentionArgs = SetObjectRetentionArgs.builder()
                .bucket(buckName)
                .object("retentionMethod/objectRetention.txt")
                .config(retention)
                // ByPass Mode（略过模式或旁路模式），泛指在一个系统的正常流程中，
                // 有一堆检核机制，而“ByPass Mode”就是当检核机制发生异常，
                // 无法在短期间内排除时，使系统作业能绕过这些检核机制，
                // 使系统能够继续运行的作业模式
                .bypassGovernanceMode(true)
                .build();

        minioClient.setObjectRetention(setObjectRetentionArgs);
        objectRetention = minioClient.getObjectRetention(getObjectRetentionArgs);
        System.out.println("修改策略后配置：" + objectRetention.mode().toString() + " " + objectRetention.retainUntilDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        System.out.println("***********************************************************************************");

    }


    @SneakyThrows
    public void objectLegalHoldOperation() {
        System.out.println("************************************对象合法保留操作***********************************************");
        System.err.println("创建bucket需要设置objectLock为true");
        uploadObject("legalHoldMethod", "objectLegalHoldMethod.txt");

        IsObjectLegalHoldEnabledArgs isObjectLegalHoldEnabledArgs = IsObjectLegalHoldEnabledArgs.builder()
                .bucket(buckName)
                .object("legalHoldMethod/objectLegalHoldMethod.txt")
                .build();

        boolean objectLegalHoldEnabled = minioClient.isObjectLegalHoldEnabled(isObjectLegalHoldEnabledArgs);
        System.out.println("执行isObjectLegalHoldEnabledArgs：" + objectLegalHoldEnabled);

        EnableObjectLegalHoldArgs enableObjectLegalHoldArgs = EnableObjectLegalHoldArgs.builder()
                .bucket(buckName)
                .object("legalHoldMethod/objectLegalHoldMethod.txt")
                .build();
        minioClient.enableObjectLegalHold(enableObjectLegalHoldArgs);

        objectLegalHoldEnabled = minioClient.isObjectLegalHoldEnabled(isObjectLegalHoldEnabledArgs);
        System.out.println("执行enableObjectLegalHold：" + objectLegalHoldEnabled);

        DisableObjectLegalHoldArgs disableObjectLegalHoldArgs = DisableObjectLegalHoldArgs.builder()
                .bucket(buckName)
                .object("legalHoldMethod/objectLegalHoldMethod.txt")
                .build();

        minioClient.disableObjectLegalHold(disableObjectLegalHoldArgs);

        objectLegalHoldEnabled = minioClient.isObjectLegalHoldEnabled(isObjectLegalHoldEnabledArgs);
        System.out.println("执行disableObjectLegalHold：" + objectLegalHoldEnabled);

        System.out.println("***********************************************************************************");

    }


    @SneakyThrows
    public void objectTagOperation() {
        System.out.println("************************************对象标签操作***********************************************");
        uploadObject("tagMethod", "objectTags.txt");
        // 与给bucket添加标签的方法是相似的
        SetObjectTagsArgs setObjectTagsArgs = SetObjectTagsArgs.builder()
                .bucket(buckName)
                .object("tagMethod/objectTags.txt")
                .tags(new HashMap<String, String>(1) {{
                    this.put("blessing", "star");
                }})
                .build();
        minioClient.setObjectTags(setObjectTagsArgs);


        GetObjectTagsArgs getObjectTagsArgs = GetObjectTagsArgs.builder()
                .bucket(buckName)
                .object("tagMethod/objectTags.txt")
                .build();
        Tags objectTags = minioClient.getObjectTags(getObjectTagsArgs);
        System.out.println("设置后的标签：" + objectTags.get());
        DeleteObjectTagsArgs deleteObjectTagsArgs = DeleteObjectTagsArgs.builder()
                .bucket(buckName)
                .object("tagMethod/objectTags.txt")
                .build();

        minioClient.deleteObjectTags(deleteObjectTagsArgs);

        objectTags = minioClient.getObjectTags(getObjectTagsArgs);
        System.out.println("删除后的标签对象：" + objectTags.get());
        System.out.println("***********************************************************************************");
    }


//    @SneakyThrows
//    public void selectObjectContent() {
//        uploadObject("selectMethod", "selectObjectContent1.txt");
//        uploadObject("selectMethod", "selectObjectContent2.txt");
//        uploadObject("selectMethod", "selectObjectContent3.txt");
//        String sqlExpression = "select * from S3Object";
//        InputSerialization is = new InputSerialization(null, false, null, null, FileHeaderInfo.USE, null, null, null);
//        OutputSerialization os = new OutputSerialization(null, null, null, QuoteFields.ASNEEDED, null);
//        SelectResponseStream stream =
//                minioClient.selectObjectContent(
//                        SelectObjectContentArgs.builder()
//                                .bucket(buckName)
//                                .object("selectMethod/selectObjectContent3.txt")
//                                .sqlExpression(sqlExpression)
//                                .inputSerialization(is)
//                                .outputSerialization(os)
//                                .requestProgress(true)
//                                .build());
//
//        byte[] buf = new byte[512];
//        int bytesRead = stream.read(buf, 0, buf.length);
//        System.out.println(new String(buf, 0, bytesRead, StandardCharsets.UTF_8));
//
//        Stats stats = stream.stats();
//        System.out.println("bytes scanned: " + stats.bytesScanned());
//        System.out.println("bytes processed: " + stats.bytesProcessed());
//        System.out.println("bytes returned: " + stats.bytesReturned());
//
//        stream.close();
//    }


    ///  The operation is not valid for the current state of the object.不知道还需要什么状态
//    @SneakyThrows
//    public void restoreObject() {
//        System.out.println("************************************restoreObject操作***********************************************");
//        uploadObject("restoreMethod", "restoreObject.txt");
////        minioClient.removeObject(RemoveObjectArgs.builder().bucket(buckName).object("restoreMethod/restoreObject.txt").build());
//
//        RestoreObjectArgs restoreObjectArgs = RestoreObjectArgs.builder()
//                .bucket(buckName)
//                .object("restoreMethod/restoreObject.txt")
//                .request(new RestoreRequest(null, null, null, null, null, null))
//                .build();
//        minioClient.restoreObject(restoreObjectArgs);
//
//        System.out.println("***********************************************************************************");
//
//    }

    @SneakyThrows
    public void uploadObject(String folder, String fileName) {
        byte[] contentBytes = MinioFileUtils.generateContent(fileName).getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contentBytes);
        PutObjectArgs thirdObjectArgs = PutObjectArgs.builder()
                .bucket(buckName)
                // 将创建一个名为putMethod文件夹
                .object(folder + "/" + fileName)
                // 两者不能同时为-1
                .stream(byteArrayInputStream, -1, PutObjectArgs.MIN_MULTIPART_SIZE)
                .build();
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(thirdObjectArgs);
    }

    @SneakyThrows
    private void putObjectByPartSize(String folder, String fileName) {
        String content = MinioFileUtils.generateContentPartSize(fileName, PutObjectArgs.MIN_MULTIPART_SIZE);
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contentBytes);
        PutObjectArgs thirdObjectArgs = PutObjectArgs.builder()
                .bucket(buckName)
                // 将创建一个名为putMethod文件夹
                .object(folder + "/" + fileName)
                // 两者不能同时为-1
                .stream(byteArrayInputStream, -1, PutObjectArgs.MIN_MULTIPART_SIZE)
                .build();
        minioClient.putObject(thirdObjectArgs);
    }


}