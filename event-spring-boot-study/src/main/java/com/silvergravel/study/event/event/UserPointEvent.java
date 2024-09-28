package com.silvergravel.study.event.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * @author DawnStar
 * @since : 2024/9/22
 */
@Getter
public class UserPointEvent extends ApplicationEvent {

    private final Integer userId;
    private final Integer orderId;

    public UserPointEvent(Object source, Integer userId, Integer orderId) {
        super(source);
        this.userId = userId;
        this.orderId = orderId;
    }

    public UserPointEvent(Object source, Clock clock, Integer userId, Integer orderId) {
        super(source, clock);
        this.userId = userId;
        this.orderId = orderId;
    }

}
