package com.silvergravel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author DawnStar
 * Date: 2023/10/7
 */
@SpringBootApplication
public class RocketMqApplication {

    public static void main(String[] args) {
//        ConfigurableApplicationContext context = new SpringApplicationBuilder(RocketMqApplication.class)
//                .web(WebApplicationType.SERVLET)
//                .run(args);
        SpringApplication.run(RocketMqApplication.class, args);
//        RocketMQTemplate rocketMQTemplate = context.getBean(RocketMQTemplate.class);
//        rocketMQTemplate.convertAndSend("silver-gravel", "Hello, World!");
//        //send spring message
//        rocketMQTemplate.send("silver-gravel", MessageBuilder.withPayload("Hello, World! I'm from spring message").build());
//        //Send messages orderly
//        rocketMQTemplate.syncSendOrderly("silver-gravel", MessageBuilder.withPayload("Hello, World").build(), "hashkey");

        //rocketMQTemplate.destroy(); // notes:  once rocketMQTemplate be destroyed, you can not send any message again with this rocketMQTemplate
    }

}

