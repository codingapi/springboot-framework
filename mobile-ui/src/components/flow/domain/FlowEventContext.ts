import React from "react";
import {FormAction} from "@/components/form";
import {FlowViewContext} from "@/components/flow/domain/FlowViewContext";
import {FlowStateContext} from "@/components/flow/domain/FlowStateContext";
import * as flowApi from "@/api/flow";
import {FlowButton, FlowUser} from "@/components/flow/types";
import {Toast} from "antd-mobile";
import {FlowSubmitResultParser} from "@/components/flow/domain/FlowResultParser";

/**
 * 流程的事件控制上下文对象
 */
export class FlowEventContext {

    private readonly flowViewContext: FlowViewContext;
    private readonly flowAction: React.RefObject<FormAction>;
    private readonly opinionAction: React.RefObject<FormAction>;
    private readonly flowStateContext: FlowStateContext;

    constructor(flowViewContext: FlowViewContext, flowAction: React.RefObject<FormAction>,opinionAction: React.RefObject<FormAction>,flowStateContext: FlowStateContext) {
        this.flowViewContext = flowViewContext;
        this.flowAction = flowAction;
        this.opinionAction = opinionAction;
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


    private validateForm = async () => {
        const formState = await this.flowAction.current?.validate();
        const opinionState = await this.opinionAction.current?.validate();
        return formState && opinionState;
    }

    /**
     * 发起流程
     * @param callback 回调函数
     */
    startFlow = (callback?: (res: any) => void) => {
        const body = this.getRequestBody();
        this.flowStateContext.setRequestLoading(true);
        flowApi.startFlow(body)
            .then(res => {
                if (res.success) {
                    const newRecordId = res.data.records[0].id;
                    this.flowStateContext.setRecordId(newRecordId);

                    if (callback) {
                        callback(res);
                    }
                }
            })
            .finally(() => {
                this.flowStateContext.setRequestLoading(false);
            })
    }


    /**
     * 提交流程
     * @param approvalState 是否审批通过
     * @param callback 回调函数
     */
    submitFlow = (approvalState: boolean, callback?: (res: any) => void) => {
        this.validateForm().then((validateState) => {
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

    /**
     * 删除流程
     * @param callback 回调函数
     */
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

    /**
     * 保存流程
     * @param callback 回调函数
     */
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

    /**
     * 延期流程
     * @param timeOut 延期时间
     * @param callback 回调函数
     */
    postponedFlow(timeOut: number, callback?: (res: any) => void) {
        this.flowStateContext.setRequestLoading(true);
        const body = {
            recordId: this.flowStateContext.getRecordId(),
            timeOut
        };
        flowApi.postponed(body).then(res => {
            if (res.success) {
                if (callback) {
                    callback(res);
                }
            }
        }).finally(() => {
            this.flowStateContext.setRequestLoading(false);
        })
    }

    /**
     * 自定义流程
     * @param button 自定义按钮
     * @param callback 回调函数
     */
    customFlow(button: FlowButton, callback?: (res: any) => void) {
        this.validateForm().then((validateState) => {
            if (validateState) {
                const body = {
                    ...this.getRequestBody(),
                    buttonId: button.id,
                }
                this.flowStateContext.setRequestLoading(true);
                flowApi.custom(body)
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

    /**
     * 转办流程
     * @param user 转办用户
     * @param callback 回调函数
     */
    transferFlow(user: FlowUser, callback?: (res: any) => void) {
        this.validateForm().then((validateState) => {
            if (validateState) {
                const body = {
                    ...this.getRequestBody(),
                    targetUserId: user.id
                }
                this.flowStateContext.setRequestLoading(true);
                flowApi.transfer(body)
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

    /**
     * 催办流程
     * @param callback
     */
    urgeFlow(callback?: (res: any) => void) {
        this.flowStateContext.setRequestLoading(true);
        const body = {
            recordId: this.flowStateContext.getRecordId()
        };
        flowApi.urge(body).then(res => {
            if (res.success) {
                if (callback) {
                    callback(res);
                }
            }
        }).finally(() => {
            this.flowStateContext.setRequestLoading(false);
        })
    }

    /**
     * 撤回流程
     */
    recallFlow(callback?: (res: any) => void) {
        this.flowStateContext.setRequestLoading(true);
        const body = {
            recordId: this.flowStateContext.getRecordId()
        };
        flowApi.recall(body).then(res => {
            if (res.success) {
                if (callback) {
                    callback(res);
                }
            }
        }).finally(() => {
            this.flowStateContext.setRequestLoading(false);
        })
    }

    /**
     * 预提交流程
     * @param callback
     */
    trySubmitFlow(callback?: (res: any) => void) {
        this.validateForm().then((validateState) => {
            if (validateState) {
                const body = {
                    ...this.getRequestBody(),
                    success: true,
                }
                this.flowStateContext.setRequestLoading(true);
                flowApi.trySubmitFlow(body)
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

    /**
     * 处理按钮点击事件
     * @param button
     */
    handlerClick(button: FlowButton) {
        if (button.type === "START") {
            this.startFlow((res) => {
                this.flowStateContext.setResult({
                    closeable: true,
                    title: '流程发起成功',
                    state: 'success',
                })
            })
        }
        if (button.type === 'SUBMIT') {
            if (this.flowStateContext.hasRecordId()) {
                this.submitFlow(true, (res) => {
                    const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
                    this.flowStateContext.setResult(flowSubmitResultParser.parser());
                })
            } else {
                this.startFlow(() => {
                    this.submitFlow(true, (res) => {
                        const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
                        this.flowStateContext.setResult(flowSubmitResultParser.parser());
                    })
                });
            }
        }

        if (button.type === 'REJECT') {
            if (this.flowStateContext.hasRecordId()) {
                this.submitFlow(false, (res) => {
                    const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
                    this.flowStateContext.setResult(flowSubmitResultParser.parser());
                })
            } else {
                this.startFlow(() => {
                    this.submitFlow(false, (res) => {
                        const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
                        this.flowStateContext.setResult(flowSubmitResultParser.parser());
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
            if (this.flowStateContext.hasRecordId()) {
                this.removeFlow(() => {
                    this.flowStateContext.setResult({
                        state: 'success',
                        closeable: true,
                        title: '流程删除成功',
                    });
                });
            } else {
                Toast.show('流程尚未发起，无法删除');
            }
        }

        if (button.type === 'RECALL') {
            this.recallFlow(() => {
                this.flowStateContext.setResult({
                    state: 'success',
                    closeable: true,
                    title: '流程撤回成功',
                });
            });
        }

        if (button.type === 'URGE') {
            this.urgeFlow(() => {
                this.flowStateContext.setResult({
                    state: 'success',
                    closeable: true,
                    title: '催办提醒已发送',
                });
            });
        }

        if (button.type === "CUSTOM") {
            this.customFlow(button, () => {
                this.flowStateContext.setResult({
                    state: 'success',
                    closeable: true,
                    title: '操作成功',
                });
            });
        }

        if(button.type === 'POSTPONED'){
            this.flowStateContext.setPostponedVisible(true);
        }
    }
}


