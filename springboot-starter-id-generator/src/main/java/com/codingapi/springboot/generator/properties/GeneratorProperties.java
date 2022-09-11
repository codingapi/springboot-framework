package com.codingapi.springboot.generator.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GeneratorProperties {

    private String jdbcUrl = "jdbc:h2:file:./generator.db";


}
