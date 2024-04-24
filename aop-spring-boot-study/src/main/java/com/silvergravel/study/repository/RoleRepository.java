package com.silvergravel.study.repository;

import com.silvergravel.study.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author DawnStar
 * @since : 2024/4/22
 */
@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
}
