package com.silvergravel.study.repository;

import com.silvergravel.study.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author DawnStar
 * @since : 2024/4/22
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

    /**
     * 根据用户ID检索权限对应关系
     * @param id 用户id条件
     * @return 返回列表
     */
    List<UserRole> findByUserId(Integer id);
}
