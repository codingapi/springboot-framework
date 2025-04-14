import React from "react";
import Markdown from "react-markdown";
import {markdown} from "@/components/flow/nodes/panel/help";
import remarkGfm from 'remark-gfm'
import FormInput from "@/components/form/input";
import FormCode from "@/components/form/code";
import "./ScriptModal.scss";
import FormInstance from "@/components/form/domain/FormInstance";
import {Modal} from "antd";
import Form from "@/components/form";

interface ScriptModalProps {
    form: FormInstance;
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onFinish: (values: any) => void;
}

const ScriptModal: React.FC<ScriptModalProps> = (props) => {
    return (
        <Modal
            width={"85vw"}
            className={"flow-script-modal"}
            title={"脚本预览"}
            open={props.visible}
            onCancel={() => {
                props.setVisible(false);
            }}
            onClose={() => {
                props.setVisible(false);
            }}
            onOk={async ()=>{
                await props.form.submit();
            }}
            destroyOnClose={true}
        >
            <Form
                form={props.form}
                onFinish={async (values) => {
                    props.onFinish(values);
                    props.setVisible(false);
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
                    </div>
                </div>
            </Form>
        </Modal>
    )
}

export default ScriptModal;
