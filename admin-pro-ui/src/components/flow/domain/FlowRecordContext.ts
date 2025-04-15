import {FlowButton, FlowFormView, FlowViewProps} from "@/components/flow/types";

/**
 * 流程的详情控制上下文对象
 */
export class FlowRecordContext {

    private readonly props: FlowViewProps;
    private readonly data: any;

    constructor(props: FlowViewProps, data?: any) {
        this.props = props;
        this.data = data;
    }

    // 获取流程的工作编码
    getWorkCode() {
        return this.data.flowWork.code;
    }

    // 获取流程的Form视图
    getFlowFormView() {
        const view = this.props.view;
        if (typeof view === 'object') {
            const nodeView = this.data.flowNode.view;
            return (view as FlowFormView)[nodeView];
        }
        return view;
    }


    // 获取节点的数据
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

    // 获取当前的节点信息
    getCurrentNode=()=>{
        const currentNodeCode = this.getNodeCode();
        return this.getNode(currentNodeCode);
    }

    // 获取当前节点的表单数据 （内部使用）
    private getNodeState = (code: string) => {
        const historyRecords = this.data.historyRecords || [];
        if (code==='over' && this.isFinished()) {
            return "done";
        }
        for (const record of historyRecords) {
            if (record.nodeCode === code) {
                if (record.flowType === 'TODO') {
                    return "current";
                }
                return "done";
            }
        }
        if(this.isFinished()){
            return "undone";
        }
        return "wait";
    }


    // 获取历史记录
    getHistoryRecords = () => {
        return this.data.historyRecords;
    }

    // 获取当前的详情的记录数据
    getCurrentFlowRecord = () => {
        return this.data.flowRecord;
    }

    // 获取当前节点的流程图
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

    // 获取审批意见
    getOpinionAdvice = () => {
        if(this.data.flowRecord){
            if(this.data.flowRecord.opinion){
                return this.data.flowRecord.opinion.advice;
            }
        }
        return null;
    }

    // 获取历史审批意见
    getHistoryOpinions() {
        if(this.data.opinions){
            return this.data.opinions.filter((item:any)=>{
                if(!item.opinion){
                    return false;
                }
                return item.opinion.result!==0;
            });
        }
        return [];
    }

    //获取流程的form数据
    getFlowFormParams() {
        return {
            ...this.data.bindData,
            ...this.props.formParams
        }
    }

    // 获取流程的操作按钮
    getFlowButtons() {
        const buttons = this.data.flowNode.buttons as FlowButton[] || [];
        return buttons.sort((item1: any, item2: any) => {
            return item1.order - item2.order;
        })
    }


    // 是否可以审批
    isApproval = () => {
        return this.data.canHandle;
    }

    // 是否是开始节点
    isStartNode = ()=>{
        if (this.data) {
            return this.data.flowNode.startNode;
        }
        return false;
    }

    // 获取当前节点的code
    getNodeCode = () => {
        if (this.data) {
            return this.data.flowNode.code;
        }
        return null;
    }

    // 获取当前节点是否可编辑
    isEditable = () => {
        if(this.isFinished()){
            return false;
        }
        if(this.isDone()){
            return false;
        }
        return this.getFlowNodeEditable()
    }

    // 获取当前节点是否可编辑
    getFlowNodeEditable = () => {
        if (this.data) {
            const node = this.data.flowNode;
            if (node) {
                return node.editable;
            }
        }
        return false
    }

    // 是否已审批
    isDone() {
        if (this.data.flowRecord) {
            return this.data.flowRecord.flowStatus === 'FINISH' || this.data.flowRecord.flowType === 'DONE';
        }
        return false;
    }


    // 是否完成
    isFinished() {
        if (this.data.flowRecord) {
            return this.data.flowRecord.flowStatus === 'FINISH';
        }
        return false;
    }


    // 是否是退回状态
    isReject(){
        const historyRecords = this.data.historyRecords || [];
        const currentRecord = this.data.flowRecord;
        if(currentRecord && historyRecords.length>0){
            const preId = currentRecord.preId;
            const preRecord = historyRecords.find((item:any)=>item.id===preId);
            if(preRecord){
                return preRecord.flowSourceDirection === 'REJECT';
            }
        }
        return false;
    }

}
