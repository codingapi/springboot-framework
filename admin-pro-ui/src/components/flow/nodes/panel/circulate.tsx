import React from "react";
import {Button, Drawer, Space, Tabs} from "antd";
import NodePanel from "@/components/flow/nodes/panel/NodePanel";
import EdgePanel from "@/components/flow/nodes/panel/EdgePanel";
import {SettingPanelProps} from "@/components/flow/types";
import {Form} from "@codingapi/form-pc";

const CirculateSettingPanel: React.FC<SettingPanelProps> = (props) => {

    const form = Form.useForm();

    return (
        <Drawer
            title={"节点设置"}
            width={"40%"}
            destroyOnClose={true}
            onClose={() => {
                props.setVisible(false);
            }}
            open={props.visible}
            extra={(
                <Space>
                    <Button
                        type={"primary"}
                        onClick={async () => {
                            await form.submit();
                            props.setVisible(false);
                        }}
                    >确认</Button>

                    <Button
                        onClick={() => {
                            props.setVisible(false);
                        }}
                    >关闭</Button>
                </Space>
            )}
        >
            <Tabs
                items={[
                    {
                        label: "节点设置",
                        key: "nodes",
                        children: (
                            <NodePanel
                                type={"circulate"}
                                form={form}
                                id={props.properties?.id}
                                data={props.properties}
                                onFinish={props.onSettingChange}
                            />
                        )
                    },
                    {
                        label: "关系设置",
                        key: "edges",
                        children: (
                            <EdgePanel
                                type={"node"}
                                id={props.properties?.id}/>
                        )
                    }
                ]}
            />

        </Drawer>
    )

}

export default CirculateSettingPanel;
