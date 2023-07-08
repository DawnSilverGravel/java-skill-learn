package com.silvergravel.intergration.service;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/7/1
 */
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
@Service
public interface MqttGateway {
    /**
     * 发送数据,没有指定Topic 默认是MqttPahoMessageHandler默认值
     * 不设置默认Qos 0
     *
     * @param data 数据
     * @see MqttPahoMessageHandler#setDefaultTopic(String)
     */
    void sendData(String data);

    /**
     * 发送数据
     *
     * @param topic 指定topic
     * @param data  数据
     */
    void sendData(String data, @Header(MqttHeaders.TOPIC) String topic);


    /**
     * 发送数据
     *
     * @param topic 指定topic
     * @param qos   消息质量等级
     * @param data  数据
     */
    void sendData(String data, @Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos);


    /**
     * 发送数据
     *
     * @param data     数据
     * @param topic    指定主题
     * @param qos      消息质量等级
     * @param retained 消息是否保留 （如果这条消息没有被消费，
     *                 那么就会保留在代理服务器上）
     */
    void sendData(String data, @Header(MqttHeaders.TOPIC) String topic
            , @Header(MqttHeaders.QOS) int qos,
                  @Header(MqttHeaders.RETAINED) boolean retained);
}
