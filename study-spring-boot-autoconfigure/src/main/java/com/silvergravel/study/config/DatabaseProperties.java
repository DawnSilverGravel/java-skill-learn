package com.silvergravel.study.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author DawnStar
 * @since : 2023/11/27
 */
@ConfigurationProperties("study.datasource")
public class DatabaseProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private Boolean enable;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
