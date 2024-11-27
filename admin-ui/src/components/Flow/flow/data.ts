import React from "react";
import {FlowFormParams, FlowFormView, FlowFormViewProps, FlowResultMessage} from "@/components/Flow/flow/types";


export class FlowWorkData {

    protected readonly data: any;

    constructor(data: any) {
        this.data = data;
    }

    getNode = (code: string) => {
        if (this.data) {
            const nodes = this.data.flowWork.nodes;
            for (const node of nodes) {
                if (node.code === code) {
                    return node;
                }
            }
        }
        return null;
    }

    getWorkCode() {
        return this.data.flowWork.code;
    }
}


export class FlowSubmitResultBuilder extends FlowWorkData {

    constructor(data: any) {
        super(data);
    }

    builder() {
        const records = this.data.records;
        const resultItems = [];
        for (const record of records) {
            const node = this.getNode(record.nodeCode);
            resultItems.push({
                label: '下级审批节点',
                value: node.name
            });

            resultItems.push({
                label: '下级审批人',
                value: record.currentOperator.name
            });
        }
        return {
            title: '流程审批完成',
            items: resultItems
        } as FlowResultMessage;
    }
}


export class FlowTrySubmitResultBuilder {

    private readonly data: any;

    constructor(data: any) {
        this.data = data;
    }

    builder() {
        const operators = this.data.operators;
        const usernames = operators.map((item: any) => {
            return item.name;
        });
        const flowResult = {
            title: '下级节点提示',
            items: [
                {
                    label: '下级审批节点',
                    value: this.data.flowNode.name
                },
                {
                    label: '下级审批人',
                    value: usernames.join(',')
                }
            ]
        }

        return flowResult as FlowResultMessage;
    }

}

export class FlowData extends FlowWorkData {
    private readonly formParams?: FlowFormParams;

    constructor(data: any, formParams?: FlowFormParams) {
        super(data);
        this.formParams = formParams;
    }

    isStartFlow = () => {
        if (this.data) {
            return this.data.flowNode.startNode;
        }
        return false;
    }

    getNodeButtons = () => {
        if (this.data) {
            return this.data.flowNode.buttons;
        }
        return null;
    }

    getCurrentNodeTitle = () => {
        if (this.data) {
            const node = this.data.flowNode;
            if (node) {
                return node.name;
            }
        }
        return null;
    }

    getFlowFormView(view: React.ComponentType<FlowFormViewProps> | FlowFormView) {
        if (typeof view === 'object') {
            const nodeView = this.data.flowNode.view;
            return (view as FlowFormView)[nodeView];
        }
        return view;
    }


    getFlowNodeEditable = () => {
        if (this.data) {
            const node = this.data.flowNode;
            if (node) {
                return node.editable;
            }
        }
        return false
    }

    getFlowData = () => {
        return {
            ...this.data.bindData,
            ...this.formParams
        }
    }

    hasData() {
        return !!this.data;
    }

}

