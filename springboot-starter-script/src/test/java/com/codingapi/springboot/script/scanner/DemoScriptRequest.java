package com.codingapi.springboot.script.scanner;

import com.codingapi.springboot.script.annotation.ScriptField;
import com.codingapi.springboot.script.annotation.ScriptFunction;
import com.codingapi.springboot.script.annotation.ScriptParameter;
import com.codingapi.springboot.script.annotation.ScriptType;

/**
 * 元数据扫描测试 fixture
 * 覆盖 @ScriptType / @ScriptField / @ScriptFunction / @ScriptParameter 的各注解分支
 */
@ScriptType(description = "demo request type")
public class DemoScriptRequest {

    /**
     * 无 name/description 覆盖的字段
     */
    @ScriptField
    private String plain;

    /**
     * 有 name/description 覆盖的字段
     */
    @ScriptField(name = "age", description = "age description")
    private int age;

    @ScriptFunction(name = "calc", description = "calc description")
    public int calc(@ScriptParameter(name = "num", description = "num description") int num) {
        return num;
    }

    /**
     * 无 description 的函数
     */
    @ScriptFunction(name = "noDesc")
    public void noDesc() {
    }
}
