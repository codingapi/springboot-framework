import React from "react";
import {FlowData} from "@/components/flow/flow/data";
import {Divider} from "antd";
import {ProDescriptions} from "@ant-design/pro-components";
import moment from "moment";
import FlowHistoryLine from "@/components/flow/flow/FlowHistoryLine";
import "./FlowHistoryLine.scss";

interface FlowHistoryProps {
    flowData:FlowData
}

const FlowHistory:React.FC<FlowHistoryProps> = (props)=>{

    const flowData = props.flowData;

    const flowRecord = flowData.getCurrentFlowRecord();

    return (
        <>
            <Divider>
                <span className="Divider-title">基本信息</span>
            </Divider>
            <div className="flowApprovalHistory">
                <ProDescriptions
                    className="proDescriptions"
                    column={2}
                    bordered
                    labelStyle={{
                        textAlign: "center",
                        width: "180px",
                    }}
                    contentStyle={{
                        width: "300px",
                    }}
                >
                    <ProDescriptions.Item
                        span={2}
                        label={"标题"}
                    >
                        <span dangerouslySetInnerHTML={{ __html: flowRecord.title }} ></span>
                    </ProDescriptions.Item>
                    <ProDescriptions.Item
                        span={1}
                        label={"发起人"}
                    >
                        {flowRecord.createOperator.name}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item
                        span={1}
                        label={"发起时间"}
                    >
                        {moment(flowRecord.createTime).format("YYYY-MM-DD HH:mm:ss")}
                    </ProDescriptions.Item>

                    <ProDescriptions.Item
                        span={1}
                        label={"状态"}
                    >
                        {flowRecord.flowStatus === 'RUNNING' && '进行中'}
                        {flowRecord.flowStatus === 'FINISH' && '已结束'}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item
                        span={1}
                        label={"流程状态"}
                    >
                        {flowRecord.recodeType === 'TODO' && '待办'}
                        {flowRecord.recodeType === 'DONE' && '已办'}
                        {flowRecord.recodeType === 'TRANSFER' && '已转办'}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item
                        span={1}
                        label={"是否延期"}
                    >
                        {flowRecord.postponedCount > 0 ? '延期' : '未延期'}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item
                        span={1}
                        label={"是否干预"}
                    >
                        {flowRecord.interfere ? '干预' : '未干预'}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item
                        span={1}
                        label={"是否已读"}
                    >
                        {flowRecord.read ? '已读' : '未读'}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item
                        span={1}
                        label={"超时时间"}
                    >
                        {flowRecord.timeoutTime == 0 ? '未设置' : moment(flowRecord.timeoutTime).format("YYYY-MM-DD HH:mm:ss")}
                    </ProDescriptions.Item>
                    <ProDescriptions.Item
                        span={1}
                        label={"节点名称"}
                    >
                        {flowData.getNode(flowRecord.nodeCode)?.name}
                    </ProDescriptions.Item>
                </ProDescriptions>
                <div className="flowApprovalHistory-RecordLine">
                    <Divider>
                        <span className="Divider-title">流程历史</span>
                    </Divider>
                    <div className="record-line">
                        <FlowHistoryLine flowData={flowData}/>
                    </div>
                </div>
            </div>
        </>
    )
}

export default FlowHistory;
