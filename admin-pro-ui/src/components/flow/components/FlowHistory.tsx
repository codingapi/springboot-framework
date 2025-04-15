import React, {useContext} from "react";
import {FlowViewReactContext} from "@/components/flow/view";
import Descriptions from "@/components/descriptions";
import {FormField} from "@/components/form/types";
import moment from "moment";
import {Tag} from "antd";

const fields = [
    {
        type: 'input',
        props: {
            name: 'title',
            label: '',
        }
    },
    {
        type: 'input',
        props: {
            name: 'createOperatorName',
            label: '发起人',
        }
    },
    {
        type: 'input',
        props: {
            name: 'createOperatorDate',
            label: '发起时间',
        }
    },
    {
        type: 'input',
        props: {
            name: 'flowStatus',
            label: '状态',
        }
    },
    {
        type: 'input',
        props: {
            name: 'recodeType',
            label: '流程状态',
        }
    },
    {
        type: 'input',
        props: {
            name: 'postponedCount',
            label: '是否延期',
        }
    },
    {
        type: 'input',
        props: {
            name: 'interfere',
            label: '是否干预',
        }
    },
    {
        type: 'input',
        props: {
            name: 'read',
            label: '是否已读',
        }
    },
    {
        type: 'input',
        props: {
            name: 'timeoutTime',
            label: '超时时间',
        }
    },
    {
        type: 'input',
        props: {
            name: 'nodeName',
            label: '节点名称',
        }
    }
] as FormField[];

const flowStatusConvert = (data: any) => {
    if (data.flowStatus === 'RUNNING') {
        return '进行中';
    }
    if (data.flowStatus === 'FINISH') {
        return '已结束';
    }
    return '';
}

const recodeTypeConvert = (data: any) => {
    if (data.flowType === 'TODO') {
        return <Tag color={'primary'}>待办</Tag>;
    }
    if (data.flowType === 'DONE') {
        return <Tag color={'success'}>已办</Tag>;
    }
    if (data.flowType === 'TRANSFER') {
        return <Tag color={'blue'}>已转办</Tag>;
    }
    return '';
}

const postponedCountConvert = (data: any) => {
    if (data.postponedCount > 0) {
        return '延期';
    }
    return '未延期';
}

const interfereConvert = (data: any) => {
    if (data.interfere) {
        return '干预';
    }
    return '未干预';
}

const readConvert = (data: any) => {
    if (data.read) {
        return '已读';
    }
    return '未读';
}

const timeoutTimeConvert = (data: any) => {
    if (data.timeoutTime == 0) {
        return '未设置';
    }
    return moment(data.timeoutTime).format("YYYY-MM-DD HH:mm:ss");
}


const FlowHistory = () => {
    const flowViewReactContext = useContext(FlowViewReactContext);
    const flowRecordContext = flowViewReactContext?.flowRecordContext;

    const currentFlowRecord = flowRecordContext?.getCurrentFlowRecord();

    if(currentFlowRecord) {
        return (
            <div className={"flow-history"}>
                <div className={"flow-basic"}>
                    <Descriptions
                        columns={fields}
                        request={async () => {
                            return {
                                ...currentFlowRecord,
                                createOperatorName: currentFlowRecord.createOperator?.name,
                                createOperatorDate: moment(currentFlowRecord.createTime).format('YYYY-MM-DD HH:mm:ss'),
                                flowStatus: flowStatusConvert(currentFlowRecord),
                                recodeType: recodeTypeConvert(currentFlowRecord),
                                postponedCount: postponedCountConvert(currentFlowRecord),
                                interfere: interfereConvert(currentFlowRecord),
                                read: readConvert(currentFlowRecord),
                                timeoutTime: timeoutTimeConvert(currentFlowRecord),
                                nodeName: flowRecordContext?.getNode(currentFlowRecord.nodeCode)?.name,
                            };
                        }}
                    />
                </div>
            </div>
        )
    }
    return <></>
}

export default FlowHistory;
