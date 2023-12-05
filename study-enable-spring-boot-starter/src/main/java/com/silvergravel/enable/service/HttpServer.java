package com.silvergravel.enable.service;

/**
 * @author DawnStar
 * @since : 2023/12/5
 */
public class HttpServer implements Server {
    @Override
    public void start() {
        System.out.println("HTTP 服务器启动...");
    }

    @Override
    public void end() {
        System.out.println("HTTP 服务器停止...");
    }
}
