package com.silvergravel.study.service;

import org.springframework.boot.WebApplicationType;

/**
 * @author DawnStar
 * @since : 2023/12/2
 */
public class WebService {
    private final WebApplicationType webApplicationType;

    public WebService(WebApplicationType webApplicationType) {
        this.webApplicationType = webApplicationType;
    }

    public void webPrint() {
        System.out.println("服务器类型："+webApplicationType.name());
    }
}
