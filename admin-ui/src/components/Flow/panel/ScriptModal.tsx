import React from "react";
import {ModalForm, ProFormText} from "@ant-design/pro-components";
import ProFormCode from "@/components/Form/ProFormCode";
import "./ScriptModal.scss";
import {EyeOutlined} from "@ant-design/icons";

interface ScriptModalProps {
    form: any;
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onFinish: (values: any) => void;
}

const ScriptModal:React.FC<ScriptModalProps> = (props)=>{

    const [show, setShow] = React.useState(false);

    return (
        <ModalForm
            form={props.form}
            title={"脚本预览"}
            width={"80%"}
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
            <ProFormText
                name={"type"}
                hidden={true}
            />

            {show && (
                <div>
                <pre>
                脚本说明：
                函数的定义必须为
                <code>
                   {
                       `
def run(content){
    // 你的代码
    return true;
}
`
                   }
                </code>
                在设置操作人是函数返回的人员的id数组：
                <code>
                   {
                       `
def run(content){
    // 你的代码
    return [1,2,3];
}
`
                   }
                </code>
                 在设置异常配置是函数返回的是人员或节点：
                <code>
                   {
                       `
def run(content){
    // 你的代码
    // return content.createNodeErrTrigger("code");
    // return content.createOperatorErrTrigger(1,2,3);
}
`
                   }
                </code>
                 在自定义标题时，返回的字符串：
                <code>
                   {
                       `
def run(content){
    // 你的代码
    return return content.getCreateOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}
}
`
                   }
                </code>
                content对象能力，content对象下存在了flowWork 流程设计对象访问方式为content.getFlowWork()，flowNode 流程节点对象访问方式为content.getFlowNode()，createOperator 创建人对象访问方式为content.getCreateOperator()，currentOperator 当前操作人对象访问方式为content.getCurrentOperator()
                获取当前表单数据对象 content.getBindData()，获取当前审批意见对象 content.getOpinion()，获取当前节点的审批历史记录数据 content.getHistoryRecords()，获取spring的bean对象 content.getBean("beanName")
            </pre>
                </div>
            )}

            <a onClick={()=>{
                setShow(!show);
            }}><EyeOutlined/> 查看帮助</a>


            <ProFormCode
                name={"script"}
                codeEditorProps={{
                    style: {
                        height: 600
                    }
                }}
            />
        </ModalForm>
    )
}

export default ScriptModal;
