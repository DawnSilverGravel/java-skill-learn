package com.silvergravel.study.event.service;

import com.silvergravel.study.event.event.UserPointEvent;
import com.silvergravel.study.event.po.OrderPO;
import com.silvergravel.study.event.repository.OrderRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author DawnStar
 * @since : 2024/9/22
 */
@Service
@Slf4j
public class OrderService {
    @Resource
    private OrderRepository orderRepository;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Transactional(rollbackFor = Exception.class)
    public void save(OrderPO orderPO) {
        orderRepository.save(orderPO);
        UserPointEvent event = new UserPointEvent(this, orderPO.getUserId(), orderPO.getId());
        applicationEventPublisher.publishEvent(event);
        log.info("order end");
    }
}
