import React, {useCallback} from "react";
import {Avatar, message} from "antd";
import HeaderDropdown from "@/components/Layout/HeaderDropdown";
import {LogoutOutlined, SettingOutlined} from "@ant-design/icons";
import {ModalForm, ProFormText} from "@ant-design/pro-components";
import {clearUser} from "@/api/account";
import {useNavigate} from "react-router-dom";


interface AvatarHeaderProps {
    props?: any;
}

const AvatarHeader: React.FC<AvatarHeaderProps> = (props) => {
    const [visible, setVisible] = React.useState(false);
    const navigate = useNavigate();

    const username = localStorage.getItem('username');
    const avatar = localStorage.getItem('avatar');

    const loginOut = () => {
        clearUser();
        navigate('/login', {replace: true});
    };

    const menuItems = [
        {
            key: 'changePwd',
            icon: <SettingOutlined/>,
            label: '修改密码',
        },
        {
            key: 'logout',
            icon: <LogoutOutlined/>,
            label: '退出登录',
        },
    ];

    const onMenuClick = useCallback(
        (event: any) => {
            const {key} = event;
            if (key === 'logout') {
                loginOut();
            }
            if (key === 'changePwd') {
                setVisible(true);
            }
        },
        [],
    );

    return (
        <>

            <HeaderDropdown
                menu={{
                    selectedKeys: [],
                    onClick: onMenuClick,
                    items: menuItems,
                }}
            >
                <div>
                    <Avatar src={avatar} style={{
                        marginRight:10
                    }}/>
                    {username}
                </div>
            </HeaderDropdown>

            <ModalForm
                open={visible}
                title="修改密码"
                modalProps={{
                    destroyOnClose: true,
                    onCancel: () => {
                        setVisible(false);
                    }
                }}
                onFinish={async (values) => {
                    const newPwd = values.newPwd;
                    const newPwd2 = values.newPwd2;
                    if (newPwd !== newPwd2) {
                        message.error('两次密码不一致');
                        return false;
                    }


                    return false;
                }}
            >
                <ProFormText.Password
                    name="oldPwd"
                    label="旧密码"
                    placeholder="请输入旧密码"
                    rules={[
                        {
                            required: true,
                            message: '请输入旧密码',
                        },
                    ]}
                />

                <ProFormText.Password
                    name="newPwd"
                    label="新密码"
                    placeholder="请输入新密码"
                    rules={[
                        {
                            required: true,
                            message: '请输入新密码',
                        },
                    ]}
                />

                <ProFormText.Password
                    name="newPwd2"
                    label="确认密码"
                    placeholder="请输入确认密码"
                    rules={[
                        {
                            required: true,
                            message: '请输入确认密码',
                        },
                    ]}
                />
            </ModalForm>

        </>
    )
}

export default AvatarHeader;