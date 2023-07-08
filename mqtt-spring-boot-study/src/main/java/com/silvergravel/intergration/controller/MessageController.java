package com.silvergravel.intergration.controller;

import com.silvergravel.intergration.service.MqttGateway;
import com.silvergravel.share.MqttProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/7/1
 */
@RestController
public class MessageController {
    @Resource
    private MqttGateway mqttGateway;

    @Resource
    private MqttProperties mqttProperties;

    @GetMapping("/integration/qos0")
    public String qos0() {
        String content = "这是integration接口的一条 QoS 0消息";
        mqttGateway.sendData(content,mqttProperties.getTopics()[0],0);
        return content;
    }

    @GetMapping("/integration/qos1")
    public String qos1() {
        String content = "这是integration接口的一条 QoS 1消息";
        mqttGateway.sendData(content,mqttProperties.getTopics()[0],1);
        return content;
    }

    @GetMapping("/integration/qos2")
    public String qos2() {
        String content = "这是integration接口的一条 QoS 2消息";
        mqttGateway.sendData(content,mqttProperties.getTopics()[0],2);
        return content;
    }

    @GetMapping("/integration/qos2/retained")
    public String retained() {
        String content = "这是integration接口的一条 QoS 2消息并且开启保留策略";
        mqttGateway.sendData(content,mqttProperties.getTopics()[0],2,true);
        return content;
    }

    @GetMapping("/integration/empty")
    public String empty() {
        mqttGateway.sendData("",mqttProperties.getTopics()[0],1,true);
        return "发送一条空消息删除保留信息，空信息是带有保留策略的空信息，无关QoS等级";
    }
}
