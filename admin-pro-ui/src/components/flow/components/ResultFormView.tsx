import React from 'react';
import {Modal, Result} from "antd";
import {ProDescriptions} from "@ant-design/pro-components";
import {FlowResultItem, ResultFormProps} from "@/components/flow/flow/types";


const ResultFormView: React.FC<ResultFormProps> = (props) => {
    const {result} = props;

    if (!result) {
        return null;
    }

    const resultStateTag = ()=>{
        const resultState = result.resultState;
        if(resultState === "SUCCESS") {
            return "success"
        }
        if(resultState === "WARNING") {
            return "warning"
        }
        return "info";
    };


    return (
        <Modal
            width={"40%"}
            height={"60%"}
            open={props.visible}
            onCancel={() => {
                props.setVisible(false);
                if (props.flowCloseable) {
                    props.closeFlow();
                }
            }}
            onClose={() => {
                props.setVisible(false);
                if (props.flowCloseable) {
                    props.closeFlow();
                }
            }}
            onOk={() => {
                props.setVisible(false);
                if (props.flowCloseable) {
                    props.closeFlow();
                }
            }}
            destroyOnClose={true}
        >

            <Result
                status={resultStateTag()}
                title={result.title}
            >
                {result.items && result.items.map((item: FlowResultItem, index: number) => {
                    return (
                        <ProDescriptions
                            column={2}
                        >
                            <ProDescriptions.Item
                                span={2}
                                label={item.label}
                                valueType="text"
                            >
                                {item.value}
                            </ProDescriptions.Item>
                        </ProDescriptions>
                    )
                })}
            </Result>
        </Modal>
    )
}

export default ResultFormView;
