package com.silvergravel.study.event.po;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author DawnStar
 * @since : 2024/9/22
 */
@Entity
@Table(name = "order", schema = "public", catalog = "learn")
public class OrderPO {
    @Basic
    @Column(name = "id", nullable = true)
    @Id
    private Integer id;
    @Basic
    @Column(name = "user_id", nullable = true)
    private Integer userId;
    @Basic
    @Column(name = "content", nullable = true, length = 60)
    private String content;
    @Basic
    @Column(name = "create_time", nullable = true)
    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderPO orderPO = (OrderPO) o;
        return Objects.equals(id, orderPO.id) && Objects.equals(userId, orderPO.userId) && Objects.equals(content, orderPO.content) && Objects.equals(createTime, orderPO.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, content, createTime);
    }
}
