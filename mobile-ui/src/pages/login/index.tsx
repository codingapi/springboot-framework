import React from "react";
import {Button, Toast} from "antd-mobile";
import {FormInput,FormPassword,Form} from "@codingapi/form-mobile";
import {useNavigate} from "react-router";
import {initUser, login} from "@/api/account";
import {config} from "@/config/theme";

const LoginPage = () => {

    const formInstance = Form.useForm();

    const navigate = useNavigate();

    const handlerLogin = async (values: any) => {
        login(values).then(res => {
            if (res.success) {
                initUser(res.data);
                navigate(config.welcomePath);
            } else {
                if (res.errMessage === 'captcha.error') {
                    Toast.show('登录失败，验证码错误！');
                    return;
                }
                Toast.show('登录失败，请检查用户名和密码！');
            }
        }).catch((error) => {
            console.error('login error', error);
            Toast.show('登录失败，请检查用户名和密码！');
        })
    }

    return (
        <div>
            <h3 style={{
                textAlign: 'center'
            }}>
                login page
            </h3>
            <Form
                form={formInstance}
                onFinish={handlerLogin}
                layout="horizontal"
                footer={(
                    <Button
                        onClick={async () => {
                            await formInstance.submit();
                        }}
                        block={true}
                        type={'button'}
                        color={'primary'}
                        size={'large'}
                    >登陆</Button>
                )}
            >

                <FormInput
                    name={'username'}
                    label={'用户名'}
                    placeholder={'请输入用户名'}
                    required={true}
                    validateFunction={async (content)=>{
                        if(content.value){
                            return []
                        }else {
                            return ["用户名不能为空"]
                        }
                    }}
                />

                <FormPassword
                    name={'password'}
                    label={'密码'}
                    placeholder={'请输入密码'}
                    required={true}
                    validateFunction={async (content)=>{
                        if(content.value){
                            return []
                        }else {
                            return ["密码不能为空"]
                        }
                    }}
                />
            </Form>
        </div>
    )
}

export default LoginPage;
