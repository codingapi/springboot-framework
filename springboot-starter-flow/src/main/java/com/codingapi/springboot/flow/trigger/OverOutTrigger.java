package com.codingapi.springboot.flow.trigger;

public class OverOutTrigger extends ScriptOutTrigger{

    public OverOutTrigger(String script, Object... params) {
        super(script, params);
    }

    public OverOutTrigger(){
        this("""
                return null;
                """);
    }
}
