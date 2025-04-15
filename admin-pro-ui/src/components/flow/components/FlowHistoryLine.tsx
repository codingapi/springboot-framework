import React, {useContext} from "react";
import {FlowViewReactContext} from "@/components/flow/view";
import moment from "moment";
import {Tag} from "antd";

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


const FlowHistoryLine = () => {
    const flowViewReactContext = useContext(FlowViewReactContext);
    const flowRecordContext = flowViewReactContext?.flowRecordContext;

    const historyRecords = flowRecordContext?.getHistoryRecords();

    return (
        <div className={"flow-history"}>
            <div className={"flow-history-list"}>
                {historyRecords && historyRecords.map((historyRecord: any, index: number) => {
                    const nodeName = flowRecordContext?.getNode(historyRecord.nodeCode)?.name;
                    const recodeType = recodeTypeConvert(historyRecord);
                    const createName = historyRecord.createOperator?.name;
                    const createTime = moment(historyRecord.createTime).format('YYYY-MM-DD HH:mm:ss');
                    const flowAdvice = historyRecord.opinion?.advice || '暂无意见';
                    return (
                        <div className={"flow-history-list-item"}>
                            <div className={"flow-history-list-item-line"}>
                                {historyRecords.length > (index + 1) &&  <div className={"flow-history-list-item-dot"}/>}
                            </div>
                            <div className={"flow-history-list-item-content"}>
                                <div className={"flow-history-list-item-title"}>{nodeName}</div>
                                <div className={"flow-history-list-item-attr"}>状态:{recodeType}</div>
                                <div className={"flow-history-list-item-attr"}>创建人:{createName}</div>
                                <div className={"flow-history-list-item-attr"}>创建时间:{createTime}</div>
                                <div className={"flow-history-list-item-attr"}>审批意见:{flowAdvice}</div>
                            </div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}

export default FlowHistoryLine;
