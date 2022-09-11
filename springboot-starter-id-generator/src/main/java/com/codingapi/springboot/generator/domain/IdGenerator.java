package com.codingapi.springboot.generator.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class IdGenerator {

    private String key;
    private long id;
    private Date updateTime;
}
