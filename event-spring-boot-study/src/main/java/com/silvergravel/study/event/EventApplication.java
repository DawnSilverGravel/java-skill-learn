package com.silvergravel.study.event;

import com.silvergravel.study.event.po.OrderPO;
import com.silvergravel.study.event.po.UserPointPO;
import com.silvergravel.study.event.repository.OrderRepository;
import com.silvergravel.study.event.repository.UserPointRepository;
import com.silvergravel.study.event.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDateTime;

/**
 * @author SilverGravel
 */
@SpringBootApplication
@EnableAsync
public class EventApplication implements ApplicationRunner {

    @Resource
    private OrderRepository orderRepository;

    @Resource
    private UserPointRepository userPointRepository;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EventApplication.class, args);
        OrderService service = context.getBean(OrderService.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        OrderPO orderPO = new OrderPO();
        orderPO.setId(2);
        // 没有该用户
        orderPO.setUserId(0);
        orderPO.setContent("测试");
        orderPO.setCreateTime(LocalDateTime.now());
        service.save(orderPO);



    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        OrderPO orderPO = new OrderPO();
        orderPO.setId(1);
        orderPO.setUserId(1);
        orderPO.setContent("测试");
        orderPO.setCreateTime(LocalDateTime.now());
        orderRepository.save(orderPO);

        UserPointPO userPointPO = new UserPointPO();
        userPointPO.setId(1);
        userPointPO.setUserId(1);
        userPointPO.setPoint(40);
        userPointRepository.save(userPointPO);

        orderRepository.deleteById(2);
    }
}