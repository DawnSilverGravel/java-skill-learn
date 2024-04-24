package com.silvergravel.study.repository;

import com.silvergravel.study.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author DawnStar
 * @since : 2024/4/22
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * 根据名称查找实体
     *
     * @param name 用户宁
     * @return 返回一个User实体
     */
    User findByName(String name);
}
