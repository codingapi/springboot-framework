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

    private loadRequestBody = () => {
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
        const body = this.loadRequestBody();
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


    submitFlow = (approvalState:boolean,callback?: (res: any) => void) => {
        this.flowAction.current?.validate().then((flag) => {
            console.log('flag', flag);
            if (flag) {
                const body = {
                    ...this.loadRequestBody(),
                    success:approvalState,
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

    handlerClick(button: FlowButton) {
        console.log('button', button);
        if (button.type === 'SUBMIT') {
            if (this.flowStateContext.hasRecordId()) {
                this.submitFlow(true,() => {
                    Toast.show('流程提交成功');
                })
            } else {
                this.startFlow(() => {
                    this.submitFlow(true,() => {
                        Toast.show('流程提交成功');
                    })
                });
            }
        }
    }
}


