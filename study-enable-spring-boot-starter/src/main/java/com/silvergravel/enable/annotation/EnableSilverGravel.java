package com.silvergravel.enable.annotation;

import com.silvergravel.enable.service.SilverGravelConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author DawnStar
 * @since : 2023/12/4
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SilverGravelConfiguration.class)
public @interface EnableSilverGravel {

}
