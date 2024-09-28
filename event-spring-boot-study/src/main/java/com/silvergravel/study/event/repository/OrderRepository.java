package com.silvergravel.study.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.silvergravel.study.event.po.OrderPO;
/**
 * @author DawnStar
 * @since : 2024/9/22
 */
public interface OrderRepository extends JpaRepository<OrderPO,Integer> {
}
