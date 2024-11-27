import React from "react";
import {FlowData} from "@/components/Flow/flow/data";
import {Button, Space} from "antd";


interface FlowButtonsProps {
    flowData: FlowData;
    requestLoading: boolean;
    setRequestLoading: (loading: boolean) => void;
    handlerClick: (item: any) => void;
}

const FlowButtons: React.FC<FlowButtonsProps> = (props) => {
    const flowData = props.flowData;

    if (!flowData) {
        return null;
    }

    const buttons = flowData.getNodeButtons();

    return (
        <Space>
            {buttons && buttons.map((item: any) => {
                const style = item.style && {
                    ...JSON.parse(item.style),
                    color: "white",
                } || {};
                return (
                    <Button
                        key={item.id}
                        onClick={() => {
                            props.handlerClick(item);
                        }}
                        loading={props.requestLoading}
                        style={{
                            ...style
                        }}
                    >
                        {item.name}
                    </Button>
                )
            })}
        </Space>
    )
}

export default FlowButtons;
