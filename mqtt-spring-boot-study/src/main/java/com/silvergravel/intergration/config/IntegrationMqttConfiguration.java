package com.silvergravel.intergration.config;

import com.silvergravel.share.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/7/1
 */
@Configuration
@IntegrationComponentScan
public class IntegrationMqttConfiguration {

    @Resource
    private MqttProperties mqttProperties;

    /**
     * 设置默认连接参数
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { "tcp://localhost:1883"});
        // 删除会话，默认true
        options.setCleanSession(false);
        options.setUserName(mqttProperties.getUsername());
        options.setPassword(mqttProperties.getPassword().toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler("silver-gravel", mqttClientFactory());
        messageHandler.setAsync(true);
        // 设置推送消息的默认主题,topic不能为null
        messageHandler.setDefaultTopic(mqttProperties.getTopics()[0]);
        // 设置推送QoS默认等级
        messageHandler.setDefaultQos(1);
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * 多个MessageProducer实例可以共用一个管道
     * 也可以使用
     * @return 一个消息管道
     */
    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttInboundChannel1() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer client1() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getBroker(),
                        mqttProperties.getIntegrationClientId(),
                        mqttProperties.getTopics());
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        // 订阅主题Qos为1
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

    @Bean
    public MessageProducer client2() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getBroker(),
                        mqttProperties.getIntegrationClientId()+1,
                        mqttProperties.getTopics()[0]);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        // 订阅主题为0
        adapter.setQos(0);
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }


    @Bean
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public MessageHandler handler() {
        // 处理topic 以及相应payload内容
        return (message -> {
            String prefix = "**************";
            String content = "\n集成Client：" +
                    "\ntopic："+message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)
                    +"\nqos："+message.getHeaders().get(MqttHeaders.RECEIVED_QOS)
                    +"\npayload："+message.getPayload()
                    +"\nretained："+message.getHeaders().get(MqttHeaders.RECEIVED_RETAINED)
                    +"\n";
            System.out.println(prefix+content+prefix);
        });
    }


//    @Bean
//    @ServiceActivator(inputChannel = "mqttInboundChannel1")
//    public MessageHandler handler1() {
//        return message -> System.out.println("handler1:"+message.getHeaders()+":"+message.getPayload());
//    }

}
