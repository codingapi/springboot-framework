import React, {useRef} from "react";
import Page from "@/components/Layout/Page";
import {ActionType, ModalForm, ProForm, ProFormSwitch, ProFormText, ProTable} from "@ant-design/pro-components";
import {changeManager, entrust, list, remove, removeEntrust, save} from "@/api/user";
import {Button, message, Popconfirm, Space} from "antd";
import {DeleteOutlined, SettingOutlined} from "@ant-design/icons";
import UserSelect from "@/pages/flow/user/select";


const UserPage = () => {

    const [visible, setVisible] = React.useState(false);
    const [selectUserVisible, setSelectUserVisible] = React.useState(false);

    const [user, setUser] = React.useState<any>({});

    const actionRef = useRef<ActionType>();
    const [form] = ProForm.useForm();

    const handleSave = (values: any) => {
        save(values).then(res => {
            if (res.success) {
                setVisible(false);
                message.success("保存成功").then();
                actionRef.current?.reload();
            }
        })
    }

    const handlerEntrust = (values: any) => {
        entrust(values).then(res => {
            if (res.success) {
                message.success("设置成功").then();
                actionRef.current?.reload();
            }
        })
    }

    const handlerRemoveEntrust = (id: any) => {
        removeEntrust(id).then(res => {
            if (res.success) {
                message.success("移除成功").then();
                actionRef.current?.reload();
            }
        })
    }

    const handlerRemove = (id: any) => {
        remove(id).then(res => {
            if (res.success) {
                message.success("删除成功").then();
                actionRef.current?.reload();
            }
        })
    }


    const handlerChangeManager = (id: any) => {
        changeManager(id).then(res => {
            if (res.success) {
                message.success("设置成功").then();
                actionRef.current?.reload();
            }
        })
    }

    const columns = [
        {
            title: '编号',
            dataIndex: 'id',
            search: false
        },
        {
            title: '姓名',
            dataIndex: 'name',
        },
        {
            title: '是否流程管理员',
            dataIndex: 'flowManager',
            render: (text: any, record: any) => {
                return (
                    <Space>
                        {record.flowManager ? "是" : "否"}

                        <Popconfirm
                            title={"确认要设置为流程管理员吗？"}
                            onConfirm={() => {
                                handlerChangeManager(record.id);
                            }}
                            okText={"确认"}
                            cancelText={"取消"}
                        >
                            <a> <SettingOutlined/></a>
                        </Popconfirm>
                    </Space>
                )
            }
        },
        {
            title: '账号名',
            dataIndex: 'username',
        },
        {
            title: '委托人',
            dataIndex: 'entrustOperatorName',
            render: (text: any, record: any) => {
                return (
                    <Space>
                        {record.entrustOperatorName}

                        <a onClick={() => {
                            setUser(record);
                            setSelectUserVisible(true);
                        }}> <SettingOutlined/></a>

                        {record.entrustOperatorId > 0 && (
                            <Popconfirm
                                title={"确认要移除委托人吗？"}
                                onConfirm={() => {
                                    handlerRemoveEntrust(record.id);
                                }}
                            >
                                <a><DeleteOutlined/></a>
                            </Popconfirm>
                        )}
                    </Space>
                )
            }
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            valueType: 'dateTime',
        },
        {
            title: '操作',
            valueType: 'option',
            render: (text: any, record: any) => {
                return [
                    <a key={"editor"} onClick={() => {
                        form.setFieldsValue(record);
                        form.setFieldValue("password", "");

                        setVisible(true);

                    }}>编辑</a>,
                    <Popconfirm
                        key={"remove"}
                        title={"确认要删除吗？"}
                        onConfirm={() => {
                            handlerRemove(record.id);
                        }}>
                        <a>删除</a>
                    </Popconfirm>
                ]
            }
        }

    ] as any[];

    return (
        <Page>
            <ProTable
                actionRef={actionRef}
                toolBarRender={() => [
                    <Button
                        type={"primary"}
                        onClick={() => {
                            setVisible(true);
                        }}
                    >创建用户</Button>
                ]}

                columns={columns}
                rowKey={"id"}
                search={false}
                request={async (params, sort, filter) => {
                    return list(params, sort, filter, []);
                }}
            />

            <ModalForm
                form={form}
                title={"编辑用户"}
                open={visible}
                modalProps={{
                    onCancel: () => {
                        setVisible(false);
                    },
                    onClose: () => {
                        setVisible(false);
                    },
                    destroyOnClose: true
                }}
                onFinish={async (values) => {
                    handleSave(values);
                }}
            >
                <ProFormText
                    name={"id"}
                    hidden={true}
                />

                <ProFormText
                    name={"name"}
                    label={"姓名"}
                    rules={[
                        {
                            required: true,
                            message: "请输入姓名"
                        }
                    ]}
                />

                <ProFormText
                    name={"username"}
                    label={"登录账号"}
                    rules={[
                        {
                            required: true,
                            message: "请输入登录账号"
                        }
                    ]}
                />

                <ProFormText
                    name={"password"}
                    label={"登录密码"}
                    rules={[
                        {
                            required: true,
                            message: "请输入登录密码"
                        }
                    ]}
                />

                <ProFormSwitch
                    name={"flowManager"}
                    label={"是否流程管理员"}
                />

            </ModalForm>

            <UserSelect
                multiple={false}
                visible={selectUserVisible}
                setVisible={setSelectUserVisible}
                onSelect={(selectedRowKeys) => {
                    if (selectedRowKeys && selectedRowKeys.length > 0) {
                        handlerEntrust({
                            id: user.id,
                            entrustUserId: selectedRowKeys[0]
                        });
                    }
                }}
            />
        </Page>
    )
}

export default UserPage;