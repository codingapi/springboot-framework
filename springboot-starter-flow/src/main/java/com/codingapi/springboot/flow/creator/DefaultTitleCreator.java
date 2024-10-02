package com.codingapi.springboot.flow.creator;

public class DefaultTitleCreator extends ScriptTitleCreator {

    public DefaultTitleCreator(String script) {
        super(script);
    }

    public DefaultTitleCreator(){
        this("""
                 if (record.getNode().isOver()) {
                    return String.format("%s-%s",
                            record.getNode().getFlowWork().getTitle(),
                            record.getNode().getName());
                }
                return String.format("%s-%s-%s",
                        record.getNode().getFlowWork().getTitle(),
                        record.getNode().getName(),
                        record.getOperatorUser().getName());
                """);
    }
}
