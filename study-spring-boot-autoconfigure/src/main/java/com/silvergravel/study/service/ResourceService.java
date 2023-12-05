package com.silvergravel.study.service;

/**
 * @author DawnStar
 * @since : 2023/12/2
 */
public class ResourceService {
    private final String resource;

    public ResourceService(String resource) {
        this.resource = resource;
    }

    public void resourcePrint() {
        System.out.println("存在资源："+resource);
    }
}
