import React from "react";
import {Button, Drawer, Space, Tabs} from "antd";
import NodePanel from "@/components/flow/nodes/panel/NodePanel";
import EdgePanel from "@/components/flow/nodes/panel/EdgePanel";
import ButtonPanel from "@/components/flow/nodes/panel/ButtonPanel";
import {SettingPanelProps} from "@/components/flow/types";
import {FormAction} from "@/components/form";


const NodeSettingPanel: React.FC<SettingPanelProps> = (props) => {

    const formAction = React.useRef<FormAction>(null);

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
                                type={"node"}
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
                                type={"node"}
                                id={props.properties?.id}/>
                        )
                    },
                ]}
            />

        </Drawer>
    )

}

export default NodeSettingPanel;
