package com.silvergravel.study.service;

import com.silvergravel.study.dto.RoleDTO;

import java.util.List;

/**
 * @author DawnStar
 * @since : 2024/4/23
 */
public interface RoleService {

    /**
     * 获取所有角色
     *
     * @return 返回RoleDTO列表
     */
    List<RoleDTO> listRole();

    /**
     * 根据角色ID获取相关信息
     *
     * @param roleIds 条件
     * @return 返回列表
     */
    List<RoleDTO> findByIds(List<Integer> roleIds);
}
