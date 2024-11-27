import React from "react";
import {Button, Space} from "antd";
import {FlowData} from "@/components/Flow/flow/data";
import FlowButtons from "@/components/Flow/flow/FlowButtons";

interface FlowTitleProps {
    setVisible: (visible: boolean) => void;
    flowData: FlowData;
    requestLoading: boolean;
    setRequestLoading: (loading: boolean) => void;
    handlerClick: (item: any) => void;
}

const FlowTitle: React.FC<FlowTitleProps> = (props) => {

    const flowData = props.flowData;

    const title = flowData.getCurrentNodeTitle();

    return (
        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <h3 style={{margin: 0}}>{title}</h3>
            <Space>

                <FlowButtons
                    flowData={flowData}
                    requestLoading={props.requestLoading}
                    setRequestLoading={props.setRequestLoading}
                    handlerClick={props.handlerClick}
                />

                <Button
                    onClick={() => {
                        props.setVisible(false);
                    }}
                >
                    关闭
                </Button>

            </Space>
        </div>
    )
}

export default FlowTitle;
