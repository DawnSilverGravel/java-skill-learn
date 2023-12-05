package com.silvergravel.enable.service;

/**
 * @author DawnStar
 * @since : 2023/12/5
 */
public interface Server {

    void start();

    void end();

    enum Type {
        /**
         * 服务器类型
         */
        HTTP,
        DNS,
        ;
    }
}
