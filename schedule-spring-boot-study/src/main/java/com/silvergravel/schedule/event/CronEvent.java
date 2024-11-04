package com.silvergravel.schedule.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author DawnStar
 * @since : 2024/11/3
 */
public class CronEvent extends ApplicationEvent {


    public CronEvent(Object source) {
        super(source);
    }
}
