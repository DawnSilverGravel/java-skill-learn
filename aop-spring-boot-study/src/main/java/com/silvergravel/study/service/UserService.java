package com.silvergravel.study.service;

import com.silvergravel.study.dto.UserDTO;

import java.util.List;

/**
 * @author DawnStar
 * @since : 2024/4/21
 */
public interface UserService {

    /**
     * 获取所有用户
     *
     * @return 返回UserDTO列表
     */
    List<UserDTO> listUser();

    /**
     * 校验用户密码
     * @param name 用户名
     * @param password 密码
     * @return 返回是否匹配
     */
    boolean checkUser(String name, String password);

    /**
     * 根据用户名称检索用户信息
     * @param name 用户名称
     * @return 返回 UserDTO实体
     */
    UserDTO findUserByName(String name);
}
