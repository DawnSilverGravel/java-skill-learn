//package com.silvergravel.normal;
//
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
///**
// * @author DawnStar
// * @since : 2023/12/25
// */
//public class ProducerExample {
//    private static final Logger logger = LoggerFactory.getLogger(ProducerExample.class);
//
//    public static void main(String[] args) throws ClientException {
//        // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8081;xxx:8081。
//        String endpoint = "localhost:8081";
//        // 消息发送的目标Topic名称，需要提前创建。
//        String topic = "TestTopic";
//        DefaultMQProducer provider = new DefaultMQProducer();
//        ClientConfigurationBuilder builder = ClientConfiguration.newBuilder().setEndpoints(endpoint);
//        ClientConfiguration configuration = builder.build();
//        // 初始化Producer时需要设置通信配置以及预绑定的Topic。
//        RocketMQProperties.Producer producer = provider.set()
//                .setTopics(topic)
//                .setClientConfiguration(configuration)
//                .build();
//        // 普通消息发送。
//        Message message = provider.newMessageBuilder()
//                .setTopic(topic)
//                // 设置消息索引键，可根据关键字精确查找某条消息。
//                .setKeys("messageKey")
//                // 设置消息Tag，用于消费端根据指定Tag过滤消息。
//                .setTag("messageTag")
//                // 消息体。
//                .setBody("messageBody".getBytes())
//                .build();
//        try {
//            // 发送消息，需要关注发送结果，并捕获失败等异常。
//            SendReceipt sendReceipt = producer.send(message);
//            logger.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
//        } catch (ClientException e) {
//            logger.error("Failed to send message", e);
//        }
//        // producer.close();
//    }
//}