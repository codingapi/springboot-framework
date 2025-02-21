import {FlowButton, FlowFormView, FlowViewProps} from "@/components/flow/types";

/**
 * 流程的详情控制上下文对象
 */
export class FlowViewContext {

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

    // 获取审批意见
    getOpinionAdvice = () => {
        if(this.data.flowRecord){
            if(this.data.flowRecord.opinion){
                return this.data.flowRecord.opinion.advice;
            }
        }
        return null;
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
