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

    canHandle = () => {
        return this.data.canHandle;
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


    getNodeState = (code: string) => {
        const historyRecords = this.data.historyRecords || [];


        if (this.isFinished()) {
            return "done";
        }

        for(const record of historyRecords){
            if(record.nodeCode === code){
                if(record.flowType==='TODO'){
                    return "wait";
                }
                return "done";
            }
        }

        return "wait";
    }

    getFlowSchema = () => {

        if (this.data.flowWork.schema) {
            const schema = JSON.parse(this.data.flowWork.schema);

            for (const node of schema.nodes) {
                node.properties.settingVisible = false;
                node.properties.state = this.getNodeState(node.properties.code);
            }
            return schema;
        }
        return null;
    }

    hasData() {
        return !!this.data;
    }

    getCurrentFlowRecord = () => {
        return this.data.flowRecord;
    }

    getHistoryRecords = () => {
        return this.data.historyRecords;
    }

    isDone() {
        if (this.data.flowRecord) {
            return this.data.flowRecord.flowStatus === 'FINISH' || this.data.flowRecord.flowType === 'DONE';
        }
        return false;
    }

    private isFinished() {
        if (this.data.flowRecord) {
            return this.data.flowRecord.flowStatus === 'FINISH';
        }
        return false;
    }

    showHistory() {
        if(this.isDone()){
            return true;
        }
        return !this.isStartFlow();
    }

    showOpinion() {
        return this.canHandle();
    }
}

