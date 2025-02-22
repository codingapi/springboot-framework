import {FlowButton} from "@/components/flow/types";
import {Toast} from "antd-mobile";
import {FlowSubmitResultParser, FlowTrySubmitResultParser} from "@/components/flow/domain/FlowResultParser";
import {FlowEventContext} from "@/components/flow/domain/FlowEventContext";
import {FlowStateContext} from "./FlowStateContext";

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
     * 处理按钮点击事件
     * @param button
     */
    handlerClick(button: FlowButton) {

        if (button.type === "RELOAD") {
            this.flowEventContext?.reloadFlow();
        }

        if (button.type === 'SAVE') {
            if (this.flowStateContext?.hasRecordId()) {
                this.flowEventContext?.saveFlow(() => {
                    Toast.show('流程保存成功');
                })
            } else {
                this.flowEventContext?.startFlow(() => {
                    this.flowEventContext?.saveFlow(() => {
                        Toast.show('流程保存成功');
                    })
                });
            }
        }

        if (button.type === "START") {
            if (this.flowStateContext?.hasRecordId()) {
                Toast.show('流程已发起，无需重复发起');
            } else {
                this.flowEventContext?.startFlow((res) => {
                    Toast.show('流程发起成功.');
                })
            }
        }
        if (button.type === 'SPECIFY_SUBMIT') {
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

        if (button.type === 'SUBMIT') {
            if (this.flowStateContext?.hasRecordId()) {
                this.flowEventContext?.submitFlow(true, (res) => {
                    const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
                    this.flowStateContext?.setResult(flowSubmitResultParser.parser());
                })
            } else {
                this.flowEventContext?.startFlow(() => {
                    this.flowEventContext?.submitFlow(true, (res) => {
                        const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
                        this.flowStateContext?.setResult(flowSubmitResultParser.parser());
                    })
                });
            }
        }

        if (button.type === 'REJECT') {
            if (this.flowStateContext?.hasRecordId()) {
                this.flowEventContext?.submitFlow(false, (res) => {
                    const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
                    this.flowStateContext?.setResult(flowSubmitResultParser.parser());
                })
            } else {
                Toast.show('流程尚未发起，无法操作');
            }
        }

        if (button.type === 'TRY_SUBMIT') {
            this.flowEventContext?.trySubmitFlow((res) => {
                const flowTrySubmitResultParser = new FlowTrySubmitResultParser(res.data);
                this.flowStateContext?.setResult(flowTrySubmitResultParser.parser());
            });
        }

        if (button.type === 'RECALL') {
            this.flowEventContext?.recallFlow(() => {
                this.flowStateContext?.setResult({
                    state: 'success',
                    closeable: true,
                    title: '流程撤回成功',
                });
            });
        }

        if (button.type === 'REMOVE') {
            if (this.flowStateContext?.hasRecordId()) {
                this.flowEventContext?.removeFlow(() => {
                    this.flowStateContext?.setResult({
                        state: 'success',
                        closeable: true,
                        title: '流程删除成功',
                    });
                });
            } else {
                Toast.show('流程尚未发起，无法删除');
            }
        }


        if (button.type === 'URGE') {
            this.flowEventContext?.urgeFlow(() => {
                this.flowStateContext?.setResult({
                    state: 'success',
                    closeable: true,
                    title: '催办提醒已发送',
                });
            });
        }

        if (button.type === 'POSTPONED') {
            this.flowStateContext?.setPostponedVisible(true);
        }


        if (button.type === 'TRANSFER') {
            this.flowStateContext?.setUserSelectMode({
                userSelectType: 'transfer',
                multiple: false,
            });
        }

        if (button.type === "CUSTOM") {
            if (this.flowStateContext?.hasRecordId()) {
                this.flowEventContext?.customFlow(button, (res) => {
                    const customMessage = res.data;
                    this.flowStateContext?.setResult({
                        state: customMessage.resultState.toLowerCase(),
                        ...customMessage
                    });
                });
            } else {
                Toast.show('流程尚未发起，无法操作');
            }
        }

        if (button.type === 'VIEW') {
            const eventKey = button.eventKey;
            this.flowEventContext?.triggerEvent(eventKey);
        }
    }
}
