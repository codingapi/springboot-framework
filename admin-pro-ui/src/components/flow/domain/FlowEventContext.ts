import {FlowRecordContext} from "@/components/flow/domain/FlowRecordContext";
import {FlowStateContext} from "@/components/flow/domain/FlowStateContext";
import * as flowApi from "@/api/flow";
import {FlowUser} from "@/components/flow/types";
import {FlowSubmitResultParser} from "@/components/flow/domain/FlowResultParser";
import {UserSelectMode} from "@/components/flow/store/FlowSlice";
import {FlowTriggerContext} from "@/components/flow/domain/FlowTriggerContext";
import FormInstance from "@/components/form/domain/FormInstance";

/**
 * 流程的事件控制上下文对象
 */
export class FlowEventContext {

    private readonly flowRecordContext: FlowRecordContext;
    private readonly flowTriggerContext: FlowTriggerContext;
    private readonly flowInstance: FormInstance;
    private readonly opinionInstance: FormInstance;
    private readonly flowStateContext: FlowStateContext;

    constructor(flowViewContext: FlowRecordContext,
                flowTriggerContext: FlowTriggerContext,
                flowInstance: FormInstance,
                opinionInstance: FormInstance,
                flowStateContext: FlowStateContext) {
        this.flowRecordContext = flowViewContext;
        this.flowTriggerContext = flowTriggerContext;
        this.flowInstance = flowInstance;
        this.opinionInstance = opinionInstance;
        this.flowStateContext = flowStateContext;
    }

    private getRequestBody = () => {
        const formData = this.flowInstance.getFieldsValue();
        const flowData = this.flowRecordContext.getFlowFormParams();
        const workCode = this.flowRecordContext.getWorkCode();
        const recordId = this.flowStateContext.getRecordId();
        const advice = this.opinionInstance.getFieldsValue();

        return {
            recordId,
            workCode,
            ...advice,
            formData: {
                ...flowData,
                ...formData,
            }
        }
    }


    private validateForm = async () => {
        const formState = await this.flowInstance.validate();
        const opinionState = await this.opinionInstance.validate();
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
     * @param operatorIds 指定审批人
     */
    submitFlow = (approvalState: boolean, callback?: (res: any) => void, operatorIds?: number[]) => {
        this.validateForm().then((validateState) => {
            if (validateState) {
                const body = {
                    ...this.getRequestBody(),
                    success: approvalState,
                    operatorIds: operatorIds,
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
     * @param buttonId 自定义按钮Id
     * @param callback 回调函数
     */
    customFlow(buttonId: string, callback?: (res: any) => void) {
        this.validateForm().then((validateState) => {
            console.log('validateState', validateState);
            if (validateState) {
                const body = {
                    ...this.getRequestBody(),
                    buttonId: buttonId,
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
     * 重新加载流程数据
     */
    reloadFlow() {
        this.flowStateContext.randomVersion();
    }


    /**
     * 触发流程事件
     */
    triggerEvent(eventKey: string) {
        this.flowTriggerContext.triggerEvent(eventKey);
    }

    /**
     * 用户选择回调
     * @param users 选择用户
     * @param userSelectMode 用户选择模式
     */
    userSelectCallback(users: FlowUser[], userSelectMode: UserSelectMode | null) {
        if (users.length > 0) {
            if (userSelectMode) {
                if (userSelectMode.userSelectType === 'transfer') {
                    const targetUser = users[0];
                    this.transferFlow(targetUser, (res) => {
                        const message = `已经成功转办给${targetUser.name}`;
                        this.flowStateContext.setResult({
                            state: 'success',
                            closeable: true,
                            title: message,
                        });
                    });
                }
                if (userSelectMode.userSelectType === 'nextNodeUser') {
                    const userIds = users.map((item: any) => {
                        return item.id;
                    });
                    this.submitFlow(true, (res) => {
                        const flowSubmitResultParser = new FlowSubmitResultParser(res.data);
                        this.flowStateContext.setResult(flowSubmitResultParser.parser());
                    }, userIds);
                }
            }
        }

        this.flowStateContext.setUserSelectVisible(false);
    }

}



