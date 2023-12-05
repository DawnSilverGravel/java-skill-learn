package com.silvergravel.study.service;

import com.silvergravel.study.config.DatabaseProperties;

/**
 * @author DawnStar
 * @since : 2023/11/27
 */
public class DatabaseService {
    private final DatabaseProperties properties;


    public DatabaseService(DatabaseProperties properties) {
        this.properties = properties;
    }

    public void print() {
        System.out.println("driver class name: " + properties.getDriverClassName());
        System.out.println("datasource url: " + properties.getUrl());
        System.out.println("username: " + properties.getUsername());
        System.out.println("password: " + properties.getPassword());
    }

}
