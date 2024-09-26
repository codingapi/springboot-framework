package com.codingapi.springboot.flow.trigger;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRecord;

public class OverOutTrigger implements IOutTrigger{

    @Override
    public FlowNode trigger(FlowRecord record) {
        return null;
    }
}
