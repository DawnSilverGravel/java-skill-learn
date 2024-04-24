package com.silvergravel.study.dto;

import lombok.Data;

import java.util.List;

/**
 * @author DawnStar
 * @since : 2024/4/22
 */
@Data
public class AuthorityDTO {

    private Integer userId;

    private String username;

    private List<String> roleNames;

}
