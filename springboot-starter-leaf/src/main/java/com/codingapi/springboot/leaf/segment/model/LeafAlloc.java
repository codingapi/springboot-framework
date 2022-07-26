package com.codingapi.springboot.leaf.segment.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class LeafAlloc {

    private String key;

    private long maxId;

    private int step;

    private Date updateTime;

}
