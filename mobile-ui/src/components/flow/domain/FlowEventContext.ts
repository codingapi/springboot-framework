import React from "react";
import {FormAction} from "@/components/form";
import {FlowViewContext} from "@/components/flow/domain/FlowViewContext";

export class FlowEventContext{

    private readonly flowViewContext:FlowViewContext;
    private readonly flowAction:React.RefObject<FormAction>;

    constructor(flowViewContext:FlowViewContext,flowAction:React.RefObject<FormAction>) {
        this.flowViewContext = flowViewContext;
        this.flowAction = flowAction;
    }

}


