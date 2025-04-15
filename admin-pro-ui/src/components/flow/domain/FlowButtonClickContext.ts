import {FlowButton} from "@/components/flow/types";
import {FlowSubmitResultParser, FlowTrySubmitResultParser} from "@/components/flow/domain/FlowResultParser";
import {FlowEventContext} from "@/components/flow/domain/FlowEventContext";
import {FlowStateContext} from "./FlowStateContext";
import {message} from "antd";

// 流程按钮事件点击触发器
export class FlowButtonClickContext {

    private readonly flowEventContext: FlowEventContext;
    private readonly flowStateContext: FlowStateContext;

    constructor(flowEventContext: FlowEventContext,
                flowStateContext: FlowStateContext) {
        this.flowEventContext = flowEventContext;
        this.flowStateContext = flowStateContext;
    }


    /**
     * 触发刷新动作
     */
    handlerReload(){
        this.flowEventContext?.reloadFlow();
    }

    /**
     * 触发保存动作
     */
    handlerSave(){
        if (this.flowStateContext?.hasRecordId()) {
            this.flowEventContext?.saveFlow(() => {
                message.info('流程保存成功').then();
            })
        } else {
            this.flowEventContext?.startFlow(() => {
                this.flowEventContext?.saveFlow(() => {
                    message.info('流程保存成功').then();
                })
            });
        }
    }

    /**
     * 触发发起动作
     */
    handlerStart(){
        if (this.flowStateContext?.hasRecordId()) {
            message.info('流程已发起，无需重复发起').then();
        } else {
            this.flowEventContext?.startFlow((res) => {
                message.info('流程发起成功.').then();
            })
        }
    }

    /**
     * 指定人提交流程
     * @param approvalState 审批状态 同意为true 驳回为false
     * @param operatorIds 流程操作人员id
     */
    handlerSpecifyOperatorSubmit(approvalState:boolean,operatorIds:number[]){
        this.flowEventContext?.submitFlow(true, (res) => {
            const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
            this.flowStateContext.setResult(flowSubmitResultParser.parser());
        }, operatorIds);
    }

    /**
     * 触发指定人提交动作
     */
    handlerSpecifySubmit(){
        const trySpecifySubmitHandler = ()=>{
            this.flowEventContext?.trySubmitFlow((res) => {
                const operators = res.data.operators;
                const userIds = operators.map((item: any) => {
                    return item.userId;
                });
                this.flowStateContext?.setUserSelectMode({
                    userSelectType: 'nextNodeUser',
                    multiple: true,
                    specifyUserIds: userIds,
                });
            });
        }
        if (this.flowStateContext?.hasRecordId()) {
            trySpecifySubmitHandler();
        }else {
            this.flowEventContext?.startFlow(() => {
                trySpecifySubmitHandler();
            });
        }
    }

    /**
     * 触发提交动作
     */
    handlerSubmit(){
        const submitHandler = ()=>{
            this.flowEventContext?.submitFlow(true, (res) => {
                const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
                this.flowStateContext?.setResult(flowSubmitResultParser.parser());
            })
        }
        if (this.flowStateContext?.hasRecordId()) {
            submitHandler();
        } else {
            this.flowEventContext?.startFlow(() => {
                submitHandler();
            });
        }
    }

    /**
     * 触发驳回动作
     */
    handlerReject(){
        if (this.flowStateContext?.hasRecordId()) {
            this.flowEventContext?.submitFlow(false, (res) => {
                const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
                this.flowStateContext?.setResult(flowSubmitResultParser.parser());
            })
        } else {
            message.info('流程尚未发起，无法操作').then();
        }
    }

    /**
     * 触发尝试提交动作
     */
    handlerTrySubmit(){
        const trySubmitHandler = ()=>{
            this.flowEventContext?.trySubmitFlow((res) => {
                const flowTrySubmitResultParser = new FlowTrySubmitResultParser(res.data);
                this.flowStateContext?.setResult(flowTrySubmitResultParser.parser());
            });
        }
        if (this.flowStateContext?.hasRecordId()) {
            trySubmitHandler();
        }else {
            this.flowEventContext?.startFlow(() => {
                trySubmitHandler();
            });
        }
    }

    /**
     * 触发撤回动作
     */
    handlerRecall(){
        this.flowEventContext?.recallFlow(() => {
            this.flowStateContext?.setResult({
                state: 'success',
                closeable: true,
                title: '流程撤回成功',
            });
        });
    }

    /**
     * 触发删除动作
     */
    handlerRemove(){
        if (this.flowStateContext?.hasRecordId()) {
            this.flowEventContext?.removeFlow(() => {
                this.flowStateContext?.setResult({
                    state: 'success',
                    closeable: true,
                    title: '流程删除成功',
                });
            });
        } else {
            message.info('流程尚未发起，无法删除').then();
        }
    }

    /**
     * 触发催办动作
     */
    handlerUrge(){
        this.flowEventContext?.urgeFlow(() => {
            this.flowStateContext?.setResult({
                state: 'success',
                closeable: true,
                title: '催办提醒已发送',
            });
        });
    }

    /**
     * 触发延期动作
     */
    handlerPostponed(){
        if (this.flowStateContext?.hasRecordId()) {
            this.flowStateContext?.setPostponedVisible(true);
        }else {
            message.info('流程尚未发起，无法操作').then();
        }
    }

    /**
     * 触发转办动作
     */
    handlerTransfer(){
        if (this.flowStateContext?.hasRecordId()) {
            this.flowStateContext?.setUserSelectMode({
                userSelectType: 'transfer',
                multiple: false,
            });
        }else {
            message.info('流程尚未发起，无法操作').then();
        }
    }

    /**
     * 触发自定义接口动作
     * @param buttonId 自定义按钮的id
     */
    handlerCustom(buttonId:string){
        const customHandler = ()=>{
            this.flowEventContext?.customFlow(buttonId, (res) => {
                const customMessage = res.data;
                this.flowStateContext?.setResult({
                    state: customMessage.resultState.toLowerCase(),
                    ...customMessage
                });
            });
        }
        if (this.flowStateContext?.hasRecordId()) {
            customHandler();
        } else {
            this.flowEventContext?.startFlow((res) => {
                customHandler();
            });
        }
    }

    /**
     * 触发自定义前端动作
     * @param eventKey 自定义按钮的事件key
     */
    handlerView(eventKey:string){
        const viewHandler = ()=>{
            this.flowEventContext?.triggerEvent(eventKey);
        }

        if (this.flowStateContext?.hasRecordId()) {
            viewHandler();
        }else {
            this.flowEventContext?.startFlow((res) => {
                viewHandler();
            });
        }
    }

    /**
     * 处理按钮点击事件
     * @param button
     */
    handlerClick(button: FlowButton) {
        if (button.type === "RELOAD") {
            this.handlerReload();
        }

        if (button.type === 'SAVE') {
            this.handlerSave();
        }

        if (button.type === "START") {
            this.handlerStart();
        }
        if (button.type === 'SPECIFY_SUBMIT') {
            this.handlerSpecifySubmit();
        }

        if (button.type === 'SUBMIT') {
            this.handlerSubmit();
        }

        if (button.type === 'REJECT') {
            this.handlerReject();
        }

        if (button.type === 'TRY_SUBMIT') {
            this.handlerTrySubmit();
        }

        if (button.type === 'RECALL') {
            this.handlerRecall();
        }

        if (button.type === 'REMOVE') {
            this.handlerRemove();
        }

        if (button.type === 'URGE') {
            this.handlerUrge()
        }

        if (button.type === 'POSTPONED') {
            this.handlerPostponed();
        }

        if (button.type === 'TRANSFER') {
            this.handlerTransfer();
        }

        if (button.type === "CUSTOM") {
            this.handlerCustom(button.id);
        }

        if (button.type === 'VIEW') {
            this.handlerView(button.eventKey);
        }
    }
}
