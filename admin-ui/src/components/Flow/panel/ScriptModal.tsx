import React from "react";
import { ModalForm, ProFormText } from "@ant-design/pro-components";
import ProFormCode from "@/components/Form/ProFormCode";
import { EyeOutlined } from "@ant-design/icons";
import "./ScriptModal.scss";

interface ScriptModalProps {
    form: any;
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onFinish: (values: any) => void;
}

const ScriptModal: React.FC<ScriptModalProps> = (props) => {

    const [show, setShow] = React.useState(false);

    return (
        <ModalForm
            className={"ScriptModal"}
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

            <a onClick={() => {
                setShow(!show);
            }}><EyeOutlined /> 查看帮助</a>

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
    return content.getCreateOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();
}
`
                            }
                        </code>
                        在自定义按钮事件时，返回createMessageResult函数：
                        <code>
                            {
                                `
def run(content){
    // 你的代码
    // 自定义返回标题
    // return content.createMessageResult('我是自定义标题');
    // 自定义返回标题并且关闭流程
    // return content.createMessageResult('我是自定义标题', true);
    // 自定义返回标题并添加现实内容
    return content.createMessageResult('我是自定义标题', true).addItem('我是标题1','我是内容2').addItem('我是标题2','我是内容2');
}
`
                            }
                        </code>
                        在自定义按钮事件时，操作流程：
                        <code>
                            {
                                `
def run(content){
    // 你的代码
    // 自定义返回标题
    // return content.createMessageResult('我是自定义标题');
    // 自定义返回标题并且关闭流程
    // return content.createMessageResult('我是自定义标题', true);
    // 提交流程
    // content.submitFlow();
    // 驳回流程
    // content.rejectFlow();
    // 催办流程
    // content.urgeFlow();
    // 保存流程
    // content.saveFlow();
    // 撤回流程
    // content.recallFlow();
    // 预提交流程
    // content.trySubmitFlow();
    // 自定义返回标题并添加现实内容
    return content.createMessageResult('我是自定义标题', true).addMessage('我是标题1','我是内容2').addMessage('我是标题2','我是内容2').closeable(false);
}
`
                            }
                        </code>
                        content对象能力，content对象下存在了flowWork 流程设计对象访问方式为content.getFlowWork()，flowNode 流程节点对象访问方式为content.getFlowNode()，createOperator 创建人对象访问方式为content.getCreateOperator()，currentOperator 当前操作人对象访问方式为content.getCurrentOperator()
                        获取当前表单数据对象 content.getBindData()，获取当前审批意见对象 content.getOpinion()，获取当前节点的审批历史记录数据 content.getHistoryRecords()，获取spring的bean对象 content.getBean("beanName")
                    </pre>
                </div>
            )}


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
