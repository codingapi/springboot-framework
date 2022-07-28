package com.codingapi.springboot.leaf.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LeafProperties {

    private String jdbcUrl = "jdbc:h2:mem:leaf;DB_CLOSE_DELAY=-1";

}
