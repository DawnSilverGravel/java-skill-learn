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
@Entity(name = "t_role")
public class Role {

    @Id
    private Integer id;

    @Column(name = "role_name")
    private String name;
}
