import React from "react";
import {UserOutlined} from "@ant-design/icons";
import {Timeline} from "antd";
import moment from "moment";
import {FlowData} from "@/components/Flow/flow/data";

interface FlowHistoryLineProps {
    flowData: FlowData
}

const FlowHistoryLine: React.FC<FlowHistoryLineProps> = (props) => {
    const flowData = props.flowData;
    const timelineData = flowData.getHistoryRecords();
    const items = timelineData.map((item: any, index: any) => {
        return {
            dot: <UserOutlined className="UserOutlined" />,
            children: (
                <div>
                    <div className="title">{flowData.getNode(item.nodeCode).name}</div>
                    <div>
                        <span>创建人：</span> <span>{item.currentOperator.name}</span>
                        {item.opinion && (
                            <span style={{ marginLeft: '10px' }}>
                                ({item.opinion.result == 1 ? '转办' : item.opinion.result == 2 ? '通过' : item.opinion.result == 3 ? '驳回' : '暂存'})
                            </span>
                        )}
                    </div>
                    <div><span>创建时间：</span> <span>{moment(item.createTime).format("YYYY-MM-DD HH:mm:ss")}</span>
                    </div>
                    <div className="opinion"><div>审批意见：</div> <div>{item.opinion?.advice || '暂无意见'}</div></div>
                </div>
            ),
            key: index,
        }
    });

    return (
        <div>
            <Timeline items={items} />
        </div>
    );
}

export default FlowHistoryLine
