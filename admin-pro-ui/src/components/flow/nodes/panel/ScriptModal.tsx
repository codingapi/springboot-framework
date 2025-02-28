import React from "react";
import Markdown from "react-markdown";
import {markdown} from "@/components/flow/nodes/panel/help";
import remarkGfm from 'remark-gfm'
import FormInput from "@/components/form/input";
import FormCode from "@/components/form/code";
import {ModalForm} from "@ant-design/pro-components";
import {FormInstance} from "antd/es/form/hooks/useForm";
import "./ScriptModal.scss";

interface ScriptModalProps {
    form: FormInstance;
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onFinish: (values: any) => void;
}

const ScriptModal: React.FC<ScriptModalProps> = (props) => {

    return (
        <ModalForm
            form={props.form}
            width={"85vw"}
            className={"flow-script-modal"}
            title={"脚本预览"}
            open={props.visible}
            modalProps={{
                onCancel: () => {
                    props.setVisible(false);
                },
                onClose: () => {
                    props.setVisible(false);
                },
                destroyOnClose: true
            }}
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
        </ModalForm>
    )
}

export default ScriptModal;
