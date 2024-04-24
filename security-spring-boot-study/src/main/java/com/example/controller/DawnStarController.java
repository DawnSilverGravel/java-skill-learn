package com.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/6/26
 */
@RestController
@RequestMapping("/dawn-star")
public class DawnStarController {

    /**
     * 登录账号
     */
    @GetMapping("/star")
    public String star() {
        return "dawnStar SecurityFilterChain处理的资源";
    }


    /**
     * 登陆user账号
     * http://localhost:8080/login?logout 退出账号
     * 登录admin账号
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String user() {
        return "dawnStar SecurityFilterChain处理的资源,需要DAWN_SILVER_GRAVEL_USER_USER权限";
    }

}
