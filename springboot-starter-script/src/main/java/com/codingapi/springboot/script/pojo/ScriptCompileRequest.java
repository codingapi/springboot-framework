package com.codingapi.springboot.script.pojo;

import lombok.Data;

@Data
public class ScriptCompileRequest {

    private boolean cache;
    private String script;
}
