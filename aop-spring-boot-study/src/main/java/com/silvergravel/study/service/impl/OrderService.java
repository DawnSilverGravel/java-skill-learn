package com.silvergravel.study.service.impl;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DawnStar
 * @since : 2023/12/19
 */
@Service
public class OrderService {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public void expose() {
        print();
    }

    public int print() {
        int nextInt = random.nextInt(10, 100);
        System.out.println(nextInt);
        return nextInt;
    }
}
