package com.silvergravel.base.controller;

import com.silvergravel.share.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/7/8
 */
@Configuration
@RestController
public class BaseController {
    @Resource
    private MqttProperties mqttProperties;

    @Resource
    private MqttClient mqttClient;

    @GetMapping("/base/qos0")
    public String qos0() throws MqttException {
        mqttClient.publish(mqttProperties.getTopics()[0], "这是一条QoS 0的消息".getBytes(), 0, false);
        return "这是一条QoS 0的消息";
    }

    @GetMapping("/base/qos1")
    public String qos1() throws MqttException {
        mqttClient.publish(mqttProperties.getTopics()[0], "这是一条QoS 1的消息".getBytes(), 1, false);
        return "这是一条QoS 1的消息";
    }


    @GetMapping("/base/qos2")
    public String qos2() throws MqttException {
        mqttClient.publish(mqttProperties.getTopics()[0], "这是一条QoS 2的消息".getBytes(), 2, false);
        return "这是一条QoS 2的消息";
    }
    @GetMapping("/base/qos2/retained")
    public String retained() throws MqttException {
        byte[] message = "这是一条QoS 2的消息 并且开启保留策略，接收之后重启系统查看retained的输出".getBytes();
        mqttClient.publish(mqttProperties.getTopics()[0],message , 2, true);
        return "这是一条QoS 2的消息 并且开启保留策略，接收之后重启系统查看retained的输出";
    }

    @GetMapping("/base/empty")
    public String empty() throws MqttException {
        mqttClient.publish(mqttProperties.getTopics()[0], "".getBytes(), 1,true);
        return "发送一条空消息删除保留信息,空信息是带有保留策略的空信息，无关QoS等级";
    }


}
