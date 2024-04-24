package com.silvergravel.study.service.impl;

import com.silvergravel.study.dto.RoleDTO;
import com.silvergravel.study.dto.UserDTO;
import com.silvergravel.study.dto.UserRoleDTO;
import com.silvergravel.study.entity.UserRole;
import com.silvergravel.study.repository.UserRoleRepository;
import com.silvergravel.study.service.RoleService;
import com.silvergravel.study.service.UserRoleService;
import com.silvergravel.study.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author DawnStar
 * @since : 2024/4/23
 */
@Service
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {

    @Resource
    private UserRoleRepository userRoleRepository;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;


    @Override
    public List<UserRoleDTO> listUserRole() {
        return userRoleRepository.findAll().stream()
                .map(userRole -> new UserRoleDTO(userRole.getId(), userRole.getUserId(), userRole.getRoleId()))
                .toList();
    }

    @Override
    public boolean hasAnyRole(String name, List<String> roles) {
        UserDTO userDTO = userService.findUserByName(name);
        if (ObjectUtils.isEmpty(userDTO)) {
            return false;
        }

        List<UserRole> userRoles = userRoleRepository.findByUserId(userDTO.id());
        if (ObjectUtils.isEmpty(userRoles)) {
            return false;
        }
        List<Integer> roleIds = userRoles.stream().map(UserRole::getRoleId).toList();
        List<RoleDTO> roleDTOList = roleService.findByIds(roleIds);
        for (RoleDTO roleDTO : roleDTOList) {
            if (roles.contains(roleDTO.name())) {
                return true;
            }
        }
        return false;
    }
}
