package com.codingapi.springboot.script.request;

import com.codingapi.springboot.script.annotation.GroovyScript;
import lombok.Data;

@Data
public class Node {

    private String id;

    @GroovyScript
    private String script;
}
