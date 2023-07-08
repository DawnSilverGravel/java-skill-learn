package com.silvergravel.share;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/7/8
 */
@ConfigurationProperties(prefix = "mqtt")
@Component
@Data
public class MqttProperties {
    private String broker;
    private String username;
    private String password;
    private String clientId;
    private String integrationClientId;
    private String[] topics;
    private Integer[] qosList;
}
