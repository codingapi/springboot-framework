import React from "react";
import {Divider} from "antd";
import {ProForm, ProFormDigit, ProFormSelect, ProFormSwitch, ProFormText} from "@ant-design/pro-components";
import ProFormCode from "@/components/Form/ProFormCode";

interface NodePanelProps {
    id?:string,
    data?:any,
    onFinish:(values:any)=>void,
    form:any,
    type:string,
}

const NodePanel:React.FC<NodePanelProps> = (props)=>{
    return (
        <ProForm
            initialValues={props.data}
            form={props.form}
            layout={"vertical"}
            onFinish={props.onFinish}
            submitter={false}
        >
            <Divider>
                基本信息
            </Divider>
            <ProFormText
                name={"name"}
                label={"节点名称"}
                rules={[
                    {
                        required: true,
                        message: "请输入节点名称"
                    }
                ]}
            />
            <ProFormText
                name={"code"}
                disabled={props.type!=='node'}
                label={"节点编码"}
                rules={[
                    {
                        required: true,
                        message: "请输入节点编码"
                    }
                ]}
            />
            <ProFormText
                name={"view"}
                label={"试图名称"}
                tooltip={"界面渲染试图的名称"}
                rules={[
                    {
                        required: true,
                        message: "请输入试图名称"
                    }
                ]}
            />

            <Divider>
                节点配置
            </Divider>

            <ProFormSelect
                name={"approvalType"}
                label={"节点类型"}
                hidden={props.type!=='node'}
                tooltip={"会签即多人审批以后再处理，非会签则是一个人处理以后即可响应"}
                rules={[
                    {
                        required: true,
                        message: "请输入节点类型"
                    }
                ]}
                options={[
                    {
                        label: "会签",
                        value: "SIGN"
                    },
                    {
                        label: "非会签",
                        value: "UN_SIGN"
                    },
                ]}
            />

            <ProFormCode
                tooltip={"操作人匹配脚本"}
                name={"operatorMatcher"}
                label={"操作人"}
            />

            <ProFormDigit
                tooltip={"超时提醒时间，单位毫米。为0时则为无超时设置"}
                fieldProps={{
                    step: 1
                }}
                name={"timeout"}
                label={"超时时间"}
            />

            <ProFormSwitch
                tooltip={"关闭编辑以后在当前节点下的流程表单无法修改数据"}
                name={"editable"}
                label={"是否编辑"}
            />

            <ProFormCode
                tooltip={"待办记录中的标题生成器脚本"}
                name={"titleGenerator"}
                label={"自定义标题"}
            />

            <Divider>
                异常配置
            </Divider>

            <ProFormCode
                tooltip={"当节点无人员匹配时的异常补偿脚本，可以指定人员或节点处理"}
                name={"errTrigger"}
                label={"异常配置"}
            />
        </ProForm>
    )
}

export default NodePanel;


