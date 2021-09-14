package com.codingapi.springboot.example.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * @author lorne
 * @since 1.0.0
 */
@Setter
@Getter
@MappedSuperclass
public abstract class BaseEntity {

    private int userId;

    private Date createTime;
}
