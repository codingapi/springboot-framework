import React from "react";
import {Button, Drawer, Space} from "antd";
import NodePanel from "@/components/flow/nodes/panel/NodePanel";
import {SettingPanelProps} from "@/components/flow/types";
import {FormAction} from "@/components/form";

const OverSettingPanel: React.FC<SettingPanelProps> = (props) => {

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
            <NodePanel
                formAction={formAction}
                type={"over"}
                id={props.properties?.id}
                data={props.properties}
                onFinish={props.onSettingChange}
            />

        </Drawer>
    )

}

export default OverSettingPanel;
