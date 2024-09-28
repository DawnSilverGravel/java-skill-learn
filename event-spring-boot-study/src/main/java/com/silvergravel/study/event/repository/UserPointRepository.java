package com.silvergravel.study.event.repository;

import com.silvergravel.study.event.po.UserPointPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author DawnStar
 * @since : 2024/9/22
 */
@Repository
public interface UserPointRepository extends JpaRepository<UserPointPO,Integer> {
}
