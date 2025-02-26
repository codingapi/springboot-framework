import {FlowResultMessage} from "@/components/flow/types";

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


export class FlowSubmitResultParser extends FlowWorkData {

    constructor(data: any) {
        super(data);
    }

    parser() {
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
            closeable: true,
            state: 'success',
            items: resultItems
        } as FlowResultMessage;
    }
}


export class FlowTrySubmitResultParser {

    private readonly data: any;

    constructor(data: any) {
        this.data = data;
    }

    parser() {
        const operators = this.data.operators;
        const usernames = operators.map((item: any) => {
            return item.name;
        });
        const flowResult = {
            title: '下级节点提示',
            closeable: false,
            state: 'success',
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
