package com.silvergravel.study.event.po;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author DawnStar
 * @since : 2024/9/22
 */
@Entity
@Table(name = "user_point", schema = "public", catalog = "learn")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class UserPointPO {
    @Column(name = "id")
    @Id
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "point")
    private Integer point;


}
