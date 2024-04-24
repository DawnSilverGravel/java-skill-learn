package com.silvergravel.study.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author DawnStar
 * @since : 2024/4/21
 */
@Data
@Entity(name = "t_user")
public class User {
    @Id
    private Integer id;

    @Column
    private String name;

    @Column
    private String password;

}
