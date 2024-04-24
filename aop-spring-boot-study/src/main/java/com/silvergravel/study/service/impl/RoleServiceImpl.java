package com.silvergravel.study.service.impl;

import com.silvergravel.study.dto.RoleDTO;
import com.silvergravel.study.entity.Role;
import com.silvergravel.study.repository.RoleRepository;
import com.silvergravel.study.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author DawnStar
 * @since : 2024/4/23
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleRepository roleRepository;

    @Override
    public List<RoleDTO> listRole() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName()))
                .toList();
    }

    @Override
    public List<RoleDTO> findByIds(List<Integer> roleIds) {
        List<Role> roles = roleRepository.findAllById(roleIds);
        return roles.stream()
                .map(role -> new RoleDTO(role.getId(), role.getName()))
                .toList();
    }
}
