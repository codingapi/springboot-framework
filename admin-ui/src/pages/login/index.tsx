import {LockOutlined, UserOutlined,} from '@ant-design/icons';
import {
    LoginForm,
    ModalForm,
    ProForm,
    ProFormCheckbox,
    ProFormText,
    ProFormTextArea,
} from '@ant-design/pro-components';
import {initUser, login} from "@/api/account";
import {useNavigate} from "react-router";
import {useEffect, useState} from "react";
import {useDispatch} from "react-redux";
import {refresh} from '@/store/MenuSlice';
import {config} from "@/config/theme";
import {message} from "antd";

const loginPage = () => {
    const navigate = useNavigate();
    const [form] = ProForm.useForm();

    const [visible, setVisible] = useState<boolean>(false);
    const dispatch = useDispatch();

    useEffect(() => {

        const isMemory = localStorage.getItem('l-isMemory');
        if (isMemory === 'true') {
            const username = localStorage.getItem('l-username');
            const password = localStorage.getItem('l-password');
            form.setFieldsValue({
                username: username,
                password: password,
                isMemory: true,
            });
        } else {
            form.setFieldsValue({
                isMemory: false,
            });
        }
    }, []);

    return (
        <>
            <LoginForm
                logo="logo.png"
                title="Admin UI"
                subTitle="Antd Admin UI Platform"
                form={form}
                onFinish={async (values) => {
                    const res = await login(values);
                    if (res.success) {
                        // 初始化用户信息
                        initUser(res.data);
                        // 重新加载菜单
                        dispatch(refresh());
                        // 跳转到欢迎页
                        navigate(config.welcomePath);

                        localStorage.setItem('l-isMemory', values.isMemory);
                        if (values.isMemory) {
                            localStorage.setItem('l-username', values.username);
                            localStorage.setItem('l-password', values.password);
                        } else {
                            localStorage.removeItem('l-username');
                            localStorage.removeItem('l-password');
                        }
                    }else{
                        message.error('登录失败，请检查用户名和密码！');
                    }
                }}
            >
                <>
                    <ProFormText
                        name="username"
                        fieldProps={{
                            size: 'large',
                            prefix: <UserOutlined className={'prefixIcon'}/>,
                        }}
                        placeholder={'用户名: admin'}
                        rules={[
                            {
                                required: true,
                                message: '请输入用户名!',
                            },
                        ]}
                    />
                    <ProFormText.Password
                        name="password"
                        fieldProps={{
                            size: 'large',
                            prefix: <LockOutlined className={'prefixIcon'}/>,
                        }}
                        placeholder={'密码: admin'}
                        rules={[
                            {
                                required: true,
                                message: '请输入密码！',
                            },
                        ]}
                    />
                </>
                <div
                    style={{
                        marginBlockEnd: 24,
                    }}
                >
                    <ProFormCheckbox noStyle name="isMemory">
                        记住密码
                    </ProFormCheckbox>

                    <a
                        style={{
                            float: 'right',
                            marginBottom: 10,
                        }}
                        onClick={() => {
                            setVisible(true);
                        }}
                    >忘记密码？</a>
                </div>
            </LoginForm>


            <ModalForm
                title="忘记管理员密码?"
                open={visible}
                modalProps={{
                    destroyOnClose: true,
                    onCancel: () => {
                        setVisible(false);
                    }
                }}
                onFinish={async (values) => {

                }}
            >
                <ProFormTextArea
                    name="code"
                    label="请输入运维口令"
                    fieldProps={{
                        rows: 6,
                    }}
                    placeholder="请输入运维口令"
                    rules={[
                        {
                            required: true,
                            message: '运维口令是必填项！',
                        },
                    ]}
                />

            </ModalForm>
        </>
    );
};


export default loginPage;
