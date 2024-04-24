package com.silvergravel.study.aop;

import java.lang.annotation.*;

/**
 * @author DawnStar
 * @since : 2024/4/21
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Role {
    /**
     * 角色名称
     * @return 返回角色
     */
    String[] roles();
}
