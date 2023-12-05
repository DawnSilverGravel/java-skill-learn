package com.silvergravel.study;

import com.silvergravel.study.service.SortServiceB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author DawnStar
 * @since : 2023/12/2
 */
@Configuration
public class StudyAutoConfigurationB {
    @Bean
    public SortServiceB serviceB() {
        return new SortServiceB();
    }

}
