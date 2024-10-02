package com.codingapi.springboot.flow.creator;

import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.script.ScriptRuntime;
import lombok.Getter;

public class ScriptTitleCreator implements ITitleCreator{

    @Getter
    private final String script;

    public ScriptTitleCreator(String script) {
        this.script = script;
    }

    @Override
    public String createTitle(FlowRecord record) {
        return ScriptRuntime.run(script, binding -> {
            binding.setVariable("record", record);
        }, String.class);
    }
}
