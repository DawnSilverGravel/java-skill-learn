package com.silvergravel.study.service;

import com.silvergravel.study.dto.UserRoleDTO;

import java.util.List;

/**
 * @author DawnStar
 * @since : 2024/4/23
 */
public interface UserRoleService {

    /**
     * 获取所有的用户角色关系
     *
     * @return 返回UserRoleDTO
     */
    List<UserRoleDTO> listUserRole();

    /**
     * 检索用户是否拥有其中一种权限
     *
     * @param name  用户名
     * @param roles 权限
     * @return 返回校验结果
     */
    boolean hasAnyRole(String name, List<String> roles);
}
