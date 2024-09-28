package com.silvergravel.study.event.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author DawnStar
 * @since : 2024/9/22
 */
public class MyEvent extends ApplicationEvent {

    public MyEvent(Object source) {
        super(source);
    }
}
