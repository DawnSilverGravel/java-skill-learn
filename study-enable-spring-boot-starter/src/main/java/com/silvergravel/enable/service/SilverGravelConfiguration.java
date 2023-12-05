package com.silvergravel.enable.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author DawnStar
 * @since : 2023/12/4
 */
@Configuration
public class SilverGravelConfiguration {

    @Bean
    public String silverGravel() {
        System.out.println("SilverGravel");
        return "SilverGravel";
    }
}
