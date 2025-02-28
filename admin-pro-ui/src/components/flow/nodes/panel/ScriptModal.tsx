import React from "react";
import {Modal} from "antd";
import Markdown from "react-markdown";
import {markdown} from "@/components/flow/nodes/panel/help";
import remarkGfm from 'remark-gfm'
import FormInput from "@/components/form/input";
import FormCode from "@/components/form/code";
import Form, {FormAction} from "@/components/form";
import "./ScriptModal.scss";

interface ScriptModalProps {
    formAction: React.RefObject<FormAction>;
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onFinish: (values: any) => void;
}

const ScriptModal: React.FC<ScriptModalProps> = (props) => {

    return (
        <Modal
            className={"flow-script-modal"}
            title={"脚本预览"}
            open={props.visible}
            onCancel={() => {
                props.setVisible(false);
            }}
            onOk={() => {
                props.formAction.current?.submit();
            }}
        >
            <div className={"flow-script-content"}>
                <div className={"flow-script-help"}>
                    <div className={"flow-script-help-markdown"}>
                        <Markdown
                            remarkPlugins={[remarkGfm]}
                        >
                            {markdown}
                        </Markdown>
                    </div>
                </div>
                <div className={"flow-script-form"}>
                    <Form
                        onFinish={async (values) => {
                            props.onFinish(values);
                            props.setVisible(false);
                        }}
                        actionRef={props.formAction}
                    >
                        <FormInput
                            name={"type"}
                            hidden={true}
                        />

                        <FormCode
                            name={"script"}
                            codeStyle={{
                                height: '65vh',
                            }}
                        />
                    </Form>
                </div>
            </div>
        </Modal>
    )
}

export default ScriptModal;
