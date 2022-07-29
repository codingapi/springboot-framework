package com.codingapi.springboot.leaf.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LeafProperties {

    private String jdbcUrl = "jdbc:h2:file:./leaf.db";

    private int defaultStep = 100;

    private int defaultMaxId = 1;

}
