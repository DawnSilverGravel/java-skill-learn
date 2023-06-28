package com.example.util;

import java.io.*;
import java.nio.file.Files;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * description:
 * 生成文件工具类
 *
 * @author DawnStar
 * date: 2023/6/15
 */
public class MinioFileUtils {
    private final static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static InputStream createFile(String fileName) {
        File file = new File(fileName);
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(file.toPath())))) {
            bufferedWriter.write("这是一个测试文件");
            return Files.newInputStream(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getUploadFile(String fileName) {
        File file = new File(fileName);
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(file.toPath())))) {
            bufferedWriter.write(generateContent(fileName));
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static String generateContent(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fileName).append("\n");
        int anInt = RANDOM.nextInt(300, 500);
        int lineLength = 50;
        for (int i = 0; i < anInt; i++) {
            for (int i1 = 0; i1 < lineLength; i1++) {
                char c = (char) RANDOM.nextInt(30, 122);
                stringBuilder.append(c);
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }


    public static String generateContentPartSize(String fileName,long partSize) {
        if (partSize < 5 * 1024 * 1024) {
            throw new IllegalArgumentException("非法参数");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fileName).append("\n");
        int lineLength = 50;
        long l = partSize / 50 + 1;
        for (long i = 0; i < l; i++) {
            for (int i1 = 0; i1 < lineLength; i1++) {
                char c = (char) RANDOM.nextInt(30, 122);
                stringBuilder.append(c);
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }


}