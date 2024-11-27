import React from 'react';
import {Modal, Result} from "antd";
import {ProDescriptions} from "@ant-design/pro-components";
import {FlowResultItem, ResultFormProps} from "@/components/Flow/flow/types";


const ResultFormView: React.FC<ResultFormProps> = (props) => {
    const {result} = props;

    if (!result) {
        return null;
    }
    return (
        <Modal
            width={"40%"}
            height={"60%"}
            open={props.visible}
            onCancel={() => {
                props.setVisible(false);
                if(props.flowCloseable){
                    props.closeFlow();
                }
            }}
            onClose={() => {
                props.setVisible(false);
                if(props.flowCloseable){
                    props.closeFlow();
                }
            }}
            onOk={() => {
                props.setVisible(false);
                if(props.flowCloseable){
                    props.closeFlow();
                }
            }}
            destroyOnClose={true}
        >

            <Result
                status="success"
                title={result.title}
            >

                {result.items && result.items.map((item: FlowResultItem, index: number) => {
                    return (
                        <ProDescriptions
                            column={2}
                        >
                            <ProDescriptions.Item
                                span={2}
                                label={item.title.label}
                                valueType="text"
                            >
                                {item.title.value}
                            </ProDescriptions.Item>

                            <ProDescriptions.Item
                                span={1}
                                label={item.message.label}
                                valueType="text"
                            >
                                {item.message.value}
                            </ProDescriptions.Item>
                        </ProDescriptions>
                    )
                })}
            </Result>
        </Modal>
    )
}

export default ResultFormView;
