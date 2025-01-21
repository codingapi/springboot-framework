import {useEffect} from "react";
import {FormInstance} from "antd/es/form/hooks/useForm";
import {FlowData, FlowSubmitResultBuilder, FlowTrySubmitResultBuilder} from "@/components/Flow/flow/data";
import {
    custom,
    postponed,
    recall,
    removeFlow,
    saveFlow,
    startFlow,
    submitFlow,
    transfer,
    trySubmitFlow,
    urge
} from "@/api/flow";
import {message} from "antd";
import {useDispatch, useSelector} from "react-redux";
import {
    clearTriggerEventClick,
    clearUserSelect,
    closeUserSelect,
    FlowReduxState,
    setUserSelectModal,
    showPostponed,
    showResult,
    triggerEventClick
} from "@/components/Flow/store/FlowSlice";
import {FlowUser} from "@/components/Flow/flow/types";


export const registerEvents = (id: string,
                               setId: (id: string) => void,
                               data: FlowData,
                               form: FormInstance<any>,
                               adviceForm: FormInstance<any>,
                               setRequestLoading: (loading: boolean) => void,
                               reload: () => void,
                               closeFlow: () => void) => {

    const timeOut = useSelector((state: FlowReduxState) => state.flow.timeOut);

    const selectUsers = useSelector((state: FlowReduxState) => state.flow.currentUsers);

    const selectUserType = useSelector((state: FlowReduxState) => state.flow.userSelectType);

    const dispatch = useDispatch();

    let recordId = id;
    // 保存流程
    const handlerSaveFlow = () => {
        const advice = adviceForm.getFieldValue('advice');
        const flowData = data.getFlowData();
        const formData = form.getFieldsValue();
        const body = {
            recordId,
            advice,
            formData: {
                ...flowData,
                ...formData,
            }
        }
        setRequestLoading(true);
        saveFlow(body).then(res => {
            if (res.success) {
                message.success('保存成功').then();
            }
        }).finally(() => {
            setRequestLoading(false)
        })
    }

    // 发起流程
    const handlerStartFlow = (callback: (recordId: string) => void) => {
        const formData = form.getFieldsValue();
        const advice = adviceForm.getFieldValue('advice');
        const flowData = data.getFlowData();
        const workCode = data.getWorkCode();
        const body = {
            workCode,
            advice: advice,
            formData: {
                ...flowData,
                ...formData,
            }
        }
        setRequestLoading(true)
        startFlow(body).then(res => {
            if (res.success) {
                const newRecordId = res.data.records[0].id;
                setId(newRecordId);
                callback(newRecordId);
            }
        }).finally(() => {
            setRequestLoading(false)
        })
    }


    // 发起并提交流程
    const handlerStartAndSubmitFlow = (callback: (res: any) => void, operatorIds?: any[]) => {
        setRequestLoading(true);
        const advice = adviceForm.getFieldValue('advice');
        const flowData = data.getFlowData();
        const workCode = data.getWorkCode();
        form.validateFields().then((formData) => {
            const body = {
                workCode,
                advice: advice,
                formData: {
                    ...flowData,
                    ...formData,
                }
            }

            startFlow(body).then(res => {
                if (res.success) {
                    const newRecordId = res.data.records[0].id;
                    const submitBody = {
                        recordId: newRecordId,
                        advice: advice,
                        operatorIds: operatorIds,
                        success: true,
                        formData: {
                            ...flowData,
                            ...formData,
                        }
                    }
                    submitFlow(submitBody).then(res => {
                        if (res.success) {
                            message.success('流程已提交').then();
                            callback(res);
                        }
                    })
                }
            }).finally(() => {
                setRequestLoading(false)
            })
        }).catch(errorInfo => {
            console.log(errorInfo);
            setRequestLoading(false);
        });
    }

    // 提交流程
    const handleSubmitFlow = (flowState: boolean, callback: (res: any) => void, operatorIds?: any[]) => {
        const advice = adviceForm.getFieldValue('advice');
        setRequestLoading(true);

        form.validateFields().then((formData) => {
            const flowData = data.getFlowData();
            const body = {
                recordId,
                advice: advice,
                operatorIds: operatorIds,
                success: flowState,
                formData: {
                    ...flowData,
                    ...formData,
                }
            }

            submitFlow(body).then(res => {
                if (res.success) {
                    message.success('流程已提交').then();
                    callback(res);
                }
            }).finally(() => {
                setRequestLoading(false)
            })
        }).catch(e => {
            console.log(e);
            setRequestLoading(false);
        })
    }

    // 自定义流程
    const handleCustomFlow = (buttonId: string) => {
        const advice = adviceForm.getFieldValue('advice');
        const formData = form.getFieldsValue();
        const flowData = data.getFlowData();
        const body = {
            recordId,
            buttonId,
            advice: advice,
            formData: {
                ...flowData,
                ...formData,
            }
        }
        custom(body).then(res => {
            if (res.success) {
                const flowResult = res.data;
                dispatch(showResult({
                    closeFlow: flowResult.closeable,
                    result: flowResult
                }));
            }
        })
    }

    // 预提交
    const handleTrySubmitFlow = (callback: (res: any) => void) => {
        setRequestLoading(true);
        form.validateFields().then((formData) => {
            const advice = adviceForm.getFieldValue('advice');
            const flowData = data.getFlowData();
            const body = {
                recordId,
                advice: advice,
                success: true,
                formData: {
                    ...flowData,
                    ...formData,
                }
            }
            trySubmitFlow(body).then(res => {
                if (res.success) {
                    callback && callback(res);
                }
            }).finally(() => {
                setRequestLoading(false);
            });
        }).catch(e => {
            console.log(e);
            setRequestLoading(false);
        });
    }

    // 撤回流程
    const handleRecallFlow = () => {
        const body = {
            recordId,
        }
        setRequestLoading(true);
        recall(body).then(res => {
            if (res.success) {
                message.success('流程已撤回').then();
                closeFlow();
            }
        }).finally(() => {
            setRequestLoading(false)
        })
    }


    // 删除流程
    const handleRemoveFlow = () => {
        const body = {
            recordId,
        }
        setRequestLoading(true);
        removeFlow(body).then(res => {
            if (res.success) {
                message.success('流程已删除').then();
                closeFlow();
            }
        }).finally(() => {
            setRequestLoading(false)
        })
    }


    // 延期流程
    const handlePostponedFlow = () => {
        const body = {
            recordId,
            timeOut: timeOut
        }
        setRequestLoading(true);
        postponed(body).then(res => {
            if (res.success) {
                message.success('已经延期').then();
            }
        }).finally(() => {
            setRequestLoading(false);
        })
    }

    // 转办流程
    const handlerTransferFlow = (user: FlowUser) => {
        const advice = adviceForm.getFieldValue('advice');
        const userId = user.id;
        setRequestLoading(true);
        form.validateFields().then((formData) => {
            const flowData = data.getFlowData();
            const body = {
                recordId,
                advice,
                targetUserId: userId,
                formData: {
                    ...flowData,
                    ...formData,
                }
            }

            transfer(body).then(res => {
                if (res.success) {
                    message.success(`已经转办给${user.name}用户`).then();
                    dispatch(clearUserSelect());
                    closeFlow();
                }
            }).finally(() => {
                setRequestLoading(false)
            })
        }).catch(e => {
            setRequestLoading(false);
            console.log(e);
        })
    }

    // 催办流程
    const handlerUrgeFlow = () => {
        const body = {
            recordId,
        }
        urge(body).then(res => {
            if (res.success) {
                message.success('催办提醒已发送').then();
            }
        })
    }


    useEffect(() => {
        if (timeOut > 0) {
            handlePostponedFlow();
        }
    }, [timeOut]);

    useEffect(() => {
        if (selectUserType === 'transfer' && selectUsers && selectUsers.length > 0) {
            const currentUser = selectUsers[0];
            handlerTransferFlow(currentUser);
        }

        if (selectUserType === 'nextNodeUser' && selectUsers && selectUsers.length > 0) {
            handleSubmitFlow(true, (res) => {
                const flowSubmitResultBuilder = new FlowSubmitResultBuilder(res.data);
                dispatch(closeUserSelect());
                dispatch(showResult({
                    closeFlow: true,
                    result: flowSubmitResultBuilder.builder()
                }));
            }, selectUsers.map((item: FlowUser) => {
                return item.id;
            }));
        }
    }, [selectUsers]);


    return (button: {
        type: string,
        id?: string,
        params?:any
    }) => {
        switch (button.type) {
            case 'RELOAD': {
                reload();
                break;
            }

            case 'SAVE': {
                // 保存流程，如果没有创建流程先创建，若已经创建则保存
                if (recordId) {
                    handlerSaveFlow();
                } else {
                    handlerStartFlow(() => {
                        message.success('流程已保存').then();
                    });
                }
                break;
            }
            case 'START': {
                handlerStartFlow(() => {
                    message.success('流程已保存至个人待办').then();
                    closeFlow();
                });
                break;
            }
            case 'SPECIFY_SUBMIT': {
                const _trySubmitFlow = () => {
                    handleTrySubmitFlow((res) => {
                        const operators = res.data.operators;
                        const userIds = operators.map((item: any) => {
                            return item.userId;
                        });
                        const approvalType = res.data.flowNode.approvalType;
                        dispatch(setUserSelectModal({
                            mode: approvalType === 'SIGN' ? 'single' : 'multiple',
                            type: 'nextNodeUser',
                            visible: true,
                            specifyUserIds: userIds
                        }));
                    });
                }
                if (recordId) {
                    _trySubmitFlow();
                } else {
                    handlerStartFlow((id) => {
                        recordId = id;
                        _trySubmitFlow();
                    });
                }
                break;
            }
            case 'SUBMIT': {
                if (recordId) {
                    handleSubmitFlow(true, (res) => {
                        const flowSubmitResultBuilder = new FlowSubmitResultBuilder(res.data);
                        dispatch(showResult({
                            closeFlow: true,
                            result: flowSubmitResultBuilder.builder()
                        }));
                    }, button.params);
                } else {
                    handlerStartAndSubmitFlow((res) => {
                        const flowSubmitResultBuilder = new FlowSubmitResultBuilder(res.data);
                        dispatch(showResult({
                            closeFlow: true,
                            result: flowSubmitResultBuilder.builder()
                        }));
                    });
                }
                break;
            }
            case 'REJECT': {
                handleSubmitFlow(false, (res) => {
                    const flowSubmitResultBuilder = new FlowSubmitResultBuilder(res.data);
                    dispatch(showResult({
                        closeFlow: true,
                        result: flowSubmitResultBuilder.builder()
                    }));
                }, button.params);
                break;
            }
            case 'TRY_SUBMIT': {
                handleTrySubmitFlow((res) => {
                    const flowSubmitResultBuilder = new FlowTrySubmitResultBuilder(res.data);
                    dispatch(showResult({
                        closeFlow: false,
                        result: flowSubmitResultBuilder.builder()
                    }));
                });
                break;
            }
            case 'RECALL': {
                handleRecallFlow();
                break;
            }
            case 'REMOVE': {
                handleRemoveFlow();
                break;
            }
            case 'URGE': {
                handlerUrgeFlow();
                break;
            }
            case 'POSTPONED': {
                dispatch(showPostponed());
                break;
            }
            case 'TRANSFER': {
                dispatch(setUserSelectModal({
                    mode: 'single',
                    type: 'transfer',
                    visible: true,
                    specifyUserIds: []
                }));
                break;
            }
            case 'CUSTOM': {
                if (button.id) {
                    const buttonId = button.id;
                    if (recordId) {
                        handleCustomFlow(buttonId);
                    } else {
                        handlerStartFlow((id) => {
                            recordId = id;
                            handleCustomFlow(buttonId);
                        });
                    }
                }
                break;
            }
            case 'VIEW': {
                if (button.id) {
                    const buttonId = button.id;
                    const customButton = data.getNodeButton(buttonId);
                    dispatch(triggerEventClick(customButton.eventKey));
                    setTimeout(() => {
                        dispatch(clearTriggerEventClick());
                    }, 300);
                }
                break;
            }
        }
    }
}
