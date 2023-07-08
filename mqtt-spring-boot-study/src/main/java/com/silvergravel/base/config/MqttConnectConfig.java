package com.silvergravel.base.config;

import com.silvergravel.share.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/7/7
 */
@Configuration
public class MqttConnectConfig {

    @Resource
    private MqttProperties mqttProperties;


    @Bean
    public MqttCallbackConfig mqttCallbackConfig() {
        return new MqttCallbackConfig();
    }

    @Bean
    public MqttClient mqttClient() throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient mqttClient = new MqttClient(mqttProperties.getBroker(),
                mqttProperties.getClientId(), persistence);
        // topics.length == qos.length 两者长度一致
        String[] topics = mqttProperties.getTopics();
        mqttClient.setCallback(mqttCallbackConfig());
        mqttClient.connect(createMqttConnectOptions());
        Integer[] qosList = mqttProperties.getQosList();
        int[] qosArray = Arrays.stream(qosList).mapToInt(Integer::intValue).toArray();
        // mqttClient开启保留策略
        mqttClient.subscribe(topics, qosArray);
        return mqttClient;
    }

    @Bean
    public MqttClient mqttClient1() throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient mqttClient = new MqttClient(mqttProperties.getBroker(),
                mqttProperties.getClientId() + 1, persistence);
        mqttClient.setCallback(mqttCallbackConfig());
        mqttClient.connect(createMqttConnectOptions());
        mqttClient.subscribe(mqttProperties.getTopics()[0], 1);
        return mqttClient;
    }


    private MqttConnectOptions createMqttConnectOptions() {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setUserName(mqttProperties.getUsername());
        connOpts.setPassword(mqttProperties.getPassword().toCharArray());
        connOpts.setCleanSession(false);
        return connOpts;
    }

}
