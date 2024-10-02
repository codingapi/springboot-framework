package com.codingapi.springboot.flow.matcher;

import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.script.ScriptRuntime;
import lombok.Getter;

import java.util.List;

public class ScriptOperatorMatcher implements IOperatorMatcher {

    @Getter
    private final String script;
    private final Object[] params;

    public ScriptOperatorMatcher(String script, Object... params) {
        this.script = script;
        this.params = params;
    }

    @Override
    public List<Long> matcherOperatorIds(FlowRecord record, IFlowOperator operator) {
        return ScriptRuntime.run(script,
                binding -> {
                    binding.setVariable("record", record);
                    binding.setVariable("operator", operator);
                    binding.setVariable("params", params);
                },
                List.class);
    }
}
