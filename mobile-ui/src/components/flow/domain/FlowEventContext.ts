import React from "react";
import {FormAction} from "@/components/form";
import {FlowViewContext} from "@/components/flow/domain/FlowViewContext";
import {FlowStateContext} from "@/components/flow/domain/FlowStateContext";
import * as flowApi from "@/api/flow";
import {FlowButton} from "@/components/flow/types";
import {Toast} from "antd-mobile";

export class FlowEventContext {

    private readonly flowViewContext: FlowViewContext;
    private readonly flowAction: React.RefObject<FormAction>;
    private readonly flowStateContext: FlowStateContext;

    constructor(flowViewContext: FlowViewContext, flowAction: React.RefObject<FormAction>, flowStateContext: FlowStateContext) {
        this.flowViewContext = flowViewContext;
        this.flowAction = flowAction;
        this.flowStateContext = flowStateContext;
    }

    private getRequestBody = () => {
        const formData = this.flowAction.current?.getFieldsValue();
        const flowData = this.flowViewContext.getFlowFormParams();
        const workCode = this.flowViewContext.getWorkCode();
        const recordId = this.flowStateContext.getRecordId();
        const advice = "";

        return {
            recordId,
            workCode,
            advice: advice,
            formData: {
                ...flowData,
                ...formData,
            }
        }

    }

    startFlow = (callback?: (res: any) => void) => {
        const body = this.getRequestBody();
        this.flowStateContext.setRequestLoading(true);
        flowApi.startFlow(body)
            .then(res => {
                if (res.success) {
                    const newRecordId = res.data.records[0].id;
                    this.flowStateContext.updateRecordId(newRecordId);

                    if (callback) {
                        callback(res);
                    }
                }
            })
            .finally(() => {
                this.flowStateContext.setRequestLoading(false);
            })
    }


    submitFlow = (approvalState: boolean, callback?: (res: any) => void) => {
        this.flowAction.current?.validate().then((validateState) => {
            if (validateState) {
                const body = {
                    ...this.getRequestBody(),
                    success: approvalState,
                }
                this.flowStateContext.setRequestLoading(true);
                flowApi.submitFlow(body)
                    .then(res => {
                        if (res.success) {
                            if (callback) {
                                callback(res);
                            }
                        }
                    })
                    .finally(() => {
                        this.flowStateContext.setRequestLoading(false);
                    })
            }
        })
    }

    removeFlow = (callback?: (res: any) => void) => {
        this.flowStateContext.setRequestLoading(true);
        const body = {
            recordId: this.flowStateContext.getRecordId()
        };
        flowApi.removeFlow(body).then(res => {
            if (res.success) {
                if (callback) {
                    callback(res);
                }
            }
        }).finally(() => {
            this.flowStateContext.setRequestLoading(false);
        })
    }

    saveFlow = (callback?: (res: any) => void) => {
        this.flowStateContext.setRequestLoading(true);
        const body = this.getRequestBody();
        flowApi.saveFlow(body).then(res => {
            if (res.success) {
                if (callback) {
                    callback(res);
                }
            }
        }).finally(() => {
            this.flowStateContext.setRequestLoading(false);
        })
    }

    handlerClick(button: FlowButton) {
        if (button.type === 'SUBMIT') {
            if (this.flowStateContext.hasRecordId()) {
                this.submitFlow(true, () => {
                    Toast.show('流程提交成功');
                })
            } else {
                this.startFlow(() => {
                    this.submitFlow(true, () => {
                        Toast.show('流程提交成功');
                    })
                });
            }
        }
        if (button.type === 'SAVE') {
            if (this.flowStateContext.hasRecordId()) {
                this.saveFlow(() => {
                    Toast.show('流程保存成功');
                })
            } else {
                this.startFlow(() => {
                    this.saveFlow(() => {
                        Toast.show('流程保存成功');
                    })
                });
            }
        }

        if (button.type === 'REMOVE') {
            console.log('hasRecordId:',this.flowStateContext.hasRecordId());
            if (this.flowStateContext.hasRecordId()) {
                this.removeFlow(() => {
                    Toast.show('流程删除成功');
                });
            } else {
                Toast.show('流程尚未发起，无法删除');
            }
        }
    }
}


