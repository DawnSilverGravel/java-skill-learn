package com.silvergravel.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/**
 * @author DawnStar
 * @since : 2023/12/26
 */
@Service
@RocketMQMessageListener(topic = "${silver-gravel}", consumerGroup = "silver-gravel")
public class RocketMqConsumer implements RocketMQListener<String>{

    @Override
    public void onMessage(String message) {
        System.out.println(message);
    }

//    public  class MyConsumer1 implements RocketMQListener<String> {
//        public void onMessage(String message) {
//        }
//    }

//    @Service
//    @RocketMQMessageListener(topic = "silver-gravel:dawn", consumerGroup = "silver-gravel")
//    public  class MyConsumer2 implements RocketMQListener<String> {
//        public void onMessage(String orderPaidEvent) {
//        }
//    }
}
