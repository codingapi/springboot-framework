package com.codingapi.springboot.flow.creator;

import com.codingapi.springboot.flow.domain.FlowRecord;

public class DefaultTitleCreator implements ITitleCreator {

    @Override
    public String createTitle(FlowRecord record) {
        if (record.getNode().isOver()) {
            return String.format("%s-%s",
                    record.getNode().getFlowWork().getTitle(),
                    record.getNode().getName());
        }
        return String.format("%s-%s-%s",
                record.getNode().getFlowWork().getTitle(),
                record.getNode().getName(),
                record.getOperatorUser().getName());
    }
}
