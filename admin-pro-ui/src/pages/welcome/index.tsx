import React from 'react';
import {PageContainer} from "@ant-design/pro-components";
import './index.scss';
import Form, {FormAction} from "@/components/form";
import FormInput from "@/components/form/input";
import {Button, Col, message, Row} from "antd";
import {FormField} from "@/components/form/types";
import FormPassword from "@/components/form/password";


const FooterButtons: React.FC<{ formAction: React.RefObject<FormAction> }> = ({formAction}) => {

    return (
        <div
            style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr",
                gridRowGap: "10px",
                gridColumnGap: "10px"
            }}
        >
            <Button
                onClick={async () => {
                    const name = formAction.current?.getFieldValue(["user", "name"])
                    message.success(name);
                }}
            >获取姓名</Button>

            <Button
                onClick={async () => {
                    formAction.current?.setFieldValue(["user", "name"], "123")
                }}
            >设置姓名</Button>

            <Button
                onClick={async () => {
                    formAction.current?.validate();
                }}
            >验证表单</Button>

            <Button
                onClick={async () => {
                    formAction.current?.submit();
                }}
            >提交表单</Button>

            <Button
                onClick={async () => {
                    const values = formAction.current?.getFieldsValue();
                    message.success(JSON.stringify(values));
                }}
            >获取表单值</Button>

            <Button
                onClick={async () => {
                    formAction.current?.reset();
                }}
            >重置表单</Button>
        </div>
    )
}

const WelcomePage = () => {

    const leftFormAction = React.useRef<FormAction>(null);
    const rightFormAction = React.useRef<FormAction>(null);

    const fields = [
        {
            type: 'input',
            props: {
                required: true,
                name: ['user', 'name'],
                label: '姓名',
                placeholder: '请输入姓名',
                validateFunction:async (content)=>{
                    const value = content.value;
                    if(value){
                        return []
                    }
                    return ['姓名不能为空']
                }
            }
        },
        {
            type: 'password',
            props: {
                required: true,
                name: ['user', 'password'],
                label: '银行卡密码',
                placeholder: '请输入银行卡密码',
                validateFunction:async (content)=>{
                    const value = content.value;
                    if(value){
                        return []
                    }
                    return ['银行卡密码不能为空']
                }
            }
        }
    ] as FormField[];

    return (
        <PageContainer>
            <Row gutter={[24, 24]}>
                <Col span={12}>
                    <Form
                        actionRef={leftFormAction}
                        footer={(
                            <FooterButtons
                                formAction={leftFormAction}
                            />
                        )}
                    >
                        <FormInput
                            required={true}
                            name={["user", "name"]}
                            label={"姓名"}
                            placeholder={"请输入姓名"}
                            validateFunction={async (content) => {
                                const value = content.value;
                                if(value){
                                    return []
                                }
                                return ['姓名不能为空']
                            }}
                        />

                        <FormPassword
                            required={true}
                            name={["user", "password"]}
                            label={"银行卡密码"}
                            placeholder={"请输入银行卡密码"}
                            validateFunction={async (content) => {
                                const value = content.value;
                                if(value){
                                    return []
                                }
                                return ['银行卡密码不能为空']
                            }}
                        />
                    </Form>
                </Col>

                <Col span={12}>
                    <Form
                        actionRef={rightFormAction}
                        footer={(
                            <FooterButtons
                                formAction={rightFormAction}
                            />
                        )}
                        loadFields={async () => {
                            return fields;
                        }}
                    >
                    </Form>
                </Col>
            </Row>

        </PageContainer>
    );
}

export default WelcomePage;
