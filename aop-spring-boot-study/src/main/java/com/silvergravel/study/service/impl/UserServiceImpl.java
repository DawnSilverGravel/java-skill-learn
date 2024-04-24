package com.silvergravel.study.service.impl;

import com.silvergravel.study.dto.UserDTO;
import com.silvergravel.study.entity.User;
import com.silvergravel.study.repository.UserRepository;
import com.silvergravel.study.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author DawnStar
 * @since : 2024/4/22
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Override
    public List<UserDTO> listUser() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getPassword()))
                .toList();
    }

    @Override
    public boolean checkUser(String name, String password) {
        User user = userRepository.findByName(name);
        return Objects.nonNull(user) && Objects.equals(user.getPassword(), password);
    }

    @Override
    public UserDTO findUserByName(String name) {
        User user = userRepository.findByName(name);
        if (user == null) {
            return null;
        }
        return new UserDTO(user.getId(), user.getName(), user.getPassword());
    }
}
