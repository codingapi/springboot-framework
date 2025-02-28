import React from "react";
import {Button, Drawer, Space, Tabs} from "antd";
import EdgePanel from "@/components/flow/nodes/panel/EdgePanel";
import NodePanel from "@/components/flow/nodes/panel/NodePanel";
import {ProForm} from "@ant-design/pro-components";
import ButtonPanel from "@/components/flow/nodes/panel/ButtonPanel";
import {SettingPanelProps} from "@/components/flow/types";
import {FormAction} from "@/components/form";

const StartSettingPanel: React.FC<SettingPanelProps> = (props) => {

    const formAction = React.useRef<FormAction>(null);

    return (
        <Drawer
            title={"节点设置"}
            width={"40%"}
            onClose={() => {
                props.setVisible(false);
            }}
            open={props.visible}
            destroyOnClose={true}
            extra={(
                <Space>
                    <Button
                        type={"primary"}
                        onClick={() => {
                            formAction.current?.submit();
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
                                type={"start"}
                                formAction={formAction}
                                id={props.properties?.id}
                                data={props.properties}
                                onFinish={props.onSettingChange}
                            />
                        )
                    },
                    {
                        label: "节点按钮",
                        key: "buttons",
                        children: (
                            <ButtonPanel
                                id={props.properties?.id}/>
                        )
                    },
                    {
                        label: "关系设置",
                        key: "edges",
                        children: (
                            <EdgePanel
                                type={"start"}
                                id={props.properties?.id}/>
                        )
                    }
                ]}
            />

        </Drawer>
    )

}

export default StartSettingPanel;
