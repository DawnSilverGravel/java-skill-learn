package com.silvergravel.study.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author DawnStar
 * @since : 2024/4/22
 */
@Data
@Entity(name = "t_user_role")
public class UserRole {
    @Id
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "role_id")
    private Integer roleId;
}
