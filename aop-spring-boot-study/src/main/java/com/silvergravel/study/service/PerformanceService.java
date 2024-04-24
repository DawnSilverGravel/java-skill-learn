package com.silvergravel.study.service;

import com.silvergravel.study.dto.AuthorityDTO;

import java.util.List;

/**
 * @author DawnStar
 * @since : 2024/4/21
 */
public interface PerformanceService {

    /**
     * 查找所有权限信息
     * @return 返回
     */
    List<AuthorityDTO> findAll();

}
