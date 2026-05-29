package com.codingapi.springboot.script.request;

import com.codingapi.springboot.script.annotation.GroovyScript;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Node {

    private String id;

    @GroovyScript
    private String script;
}
