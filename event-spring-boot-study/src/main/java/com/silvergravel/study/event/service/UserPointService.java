package com.silvergravel.study.event.service;

import com.silvergravel.study.event.po.UserPointPO;
import com.silvergravel.study.event.repository.UserPointRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author DawnStar
 * @since : 2024/9/22
 */
@Service
@Slf4j
public class UserPointService {
    @Resource
    private UserPointRepository userPointRepository;

    public void save(UserPointPO userPointPO) {
        log.info("保存...");
        userPointRepository.saveAndFlush(userPointPO);
    }

    public UserPointPO query(Integer userId) {
        // 假设没有找到就爆粗哟
        UserPointPO userPointPO = new UserPointPO();
        userPointPO.setUserId(userId);
        Optional<UserPointPO> one = userPointRepository.findOne(Example.of(userPointPO));
        if (one.isPresent()) {
            return one.get();
        }
        throw new RuntimeException("故意报错");
    }
}
