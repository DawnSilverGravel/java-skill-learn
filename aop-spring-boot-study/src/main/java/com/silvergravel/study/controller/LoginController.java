package com.silvergravel.study.controller;

import com.silvergravel.study.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author DawnStar
 * @since : 2024/4/21
 */
@RestController
public class LoginController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public String login(String name, String password, HttpServletResponse response) {
       boolean condition =  userService.checkUser(name, password);
        if (condition) {
            Cookie cookie = new Cookie("Authorization", name);
            response.addCookie(cookie);
            return "登陆成功";
        }
        return "用户或密码名错误";
    }
}
