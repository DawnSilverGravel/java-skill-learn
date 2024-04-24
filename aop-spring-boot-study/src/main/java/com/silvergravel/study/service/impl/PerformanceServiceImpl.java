package com.silvergravel.study.service.impl;

import com.silvergravel.study.dto.AuthorityDTO;
import com.silvergravel.study.dto.RoleDTO;
import com.silvergravel.study.dto.UserDTO;
import com.silvergravel.study.dto.UserRoleDTO;
import com.silvergravel.study.service.PerformanceService;
import com.silvergravel.study.service.RoleService;
import com.silvergravel.study.service.UserRoleService;
import com.silvergravel.study.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author DawnStar
 * @since : 2024/4/23
 */
@Service
public class PerformanceServiceImpl implements PerformanceService {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;


    @Override
    public List<AuthorityDTO> findAll() {
        List<UserDTO> userDTOList = userService.listUser();
        List<RoleDTO> roleDTOList = roleService.listRole();
        List<UserRoleDTO> userRoleDTOList = userRoleService.listUserRole();
        Map<Integer, RoleDTO> roleDTOMap = roleDTOList.stream().collect(Collectors.toMap(RoleDTO::id, Function.identity()));
        Map<Integer, List<UserRoleDTO>> userRoleDTOMap = userRoleDTOList.stream().collect(Collectors.groupingBy(UserRoleDTO::userId));
        List<AuthorityDTO> authorityDTOList = new ArrayList<>();
        userDTOList.forEach(
                userDTO -> {
                    List<UserRoleDTO> currentUserRoleList = userRoleDTOMap.get(userDTO.id());
                    AuthorityDTO authorityDTO = new AuthorityDTO();
                    authorityDTO.setUserId(userDTO.id());
                    authorityDTO.setUsername(userDTO.name());
                    List<String> roleNames = currentUserRoleList.stream()
                            .map(userRoleDTO -> {
                                RoleDTO roleDTO = roleDTOMap.get(userRoleDTO.roleId());
                                if (Objects.isNull(roleDTO)) {
                                    return null;
                                }
                                return roleDTO.name();
                            })
                            .filter(s -> !Objects.isNull(s))
                            .toList();
                    authorityDTO.setRoleNames(roleNames);
                    authorityDTOList.add(authorityDTO);
                }
        );
        return authorityDTOList;
    }
}
