package com.silvergravel.codec;

import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * @author DawnStar
 * @since : 2023/10/23
 */
@Data
public class DataProtocol{
    /**
     * 长度
     */
    private Integer typeLength;
    private Integer jsonLength;

    /**
     * 类型
     */
    private String type;

    /**
     * json
     */
    private String json;

    public static DataProtocol createProtocol(Class<?> clazz,String json) {
        DataProtocol dataProtocol = new DataProtocol();
        dataProtocol.setJson(json);
        dataProtocol.setType(clazz.getName());
        System.out.println(clazz.getName());
        dataProtocol.setJsonLength(json.getBytes(StandardCharsets.UTF_8).length);
        dataProtocol.setTypeLength(clazz.getName().getBytes(StandardCharsets.UTF_8).length);
        return dataProtocol;
    }
}
