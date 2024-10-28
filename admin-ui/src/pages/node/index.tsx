import {del, list, save} from '@/api/node';
import {PlusOutlined} from '@ant-design/icons';
import type {ActionType, ProColumns} from '@ant-design/pro-components';
import {ModalForm, PageContainer, ProFormSelect, ProFormText, ProTable} from '@ant-design/pro-components';
import {Button, Form, message, Popconfirm} from 'antd';
import React, {useRef, useState} from 'react';

const NodePage: React.FC = () => {

    const actionRef = useRef<ActionType>(null);
    const [form] = Form.useForm();
    const [createModalOpen, handleModalOpen] = useState<boolean>(false);

    const handleAdd = async (fields: any) => {
        const hide = message.loading('正在添加');
        try {
            await save({...fields});
            hide();
            message.success('保存成功');
            return true;
        } catch (error) {
            hide();
            message.error('保存失败，请重试!');
            return false;
        }
    };


    const handleDel = async (id: string) => {
        const hide = message.loading('正在删除');
        try {
            await del({id: id});
            hide();
            message.success('删除成功');
            if (actionRef.current) {
                actionRef?.current.reload();
            }
            return true;
        } catch (error) {
            hide();
            message.error('删除失败，请重试!');
            return false;
        }
    };

    const columns: ProColumns<any>[] = [
        {
            title: "编号",
            dataIndex: 'id',
            key: 'id',
            sorter: true,
            search: false,
        },
        {
            title: "服务名称",
            dataIndex: 'name',
            key: 'name',
            sorter: true,
        },
        {
            title: "地址",
            dataIndex: 'url',
            search: false,
            key: 'url',
        },
        {
            title: "状态",
            dataIndex: 'state',
            key: 'state',
            filters: [
                {
                    text: '禁用',
                    value: 0,
                },
                {
                    text: '启用',
                    value: 1,
                },
            ],
            valueEnum: {
                0: {
                    text: '禁用',
                    status: 'Error'
                },
                1: {
                    text: '启用',
                    status: 'Success'
                },
            },
        },
        {
            title: "操作",
            dataIndex: 'option',
            valueType: 'option',
            key: 'option',
            render: (_, record) => [
                <a
                    key="config"
                    onClick={() => {
                        form.setFieldsValue(record)
                        handleModalOpen(true);
                    }}
                >
                    修改
                </a>,
                <Popconfirm
                    key="delete"
                    title="删除提示"
                    description="确认要删除这条数据吗?"
                    onConfirm={async () => {
                        await handleDel(record.id);
                    }}
                    okText="确认"
                    cancelText="取消"
                >
                    <a key="delete">
                        删除
                    </a>
                </Popconfirm>
            ],
        },
    ];

    return (
        <PageContainer>
            <ProTable
                headerTitle="服务节点配置"
                actionRef={actionRef}
                columns={columns}
                rowKey="id"

                toolBarRender={() => [
                    <Button
                        type="primary"
                        key="primary"
                        onClick={() => {
                            handleModalOpen(true);
                        }}
                    >
                        <PlusOutlined/> 新建
                    </Button>
                ]}
                request={async (params, sort, filter) => {
                    return await list(params, sort, filter, [
                        {
                            key: 'name',
                            type: 'LIKE'
                        }
                    ]);
                }
                }
            />

            <ModalForm
                title="新建规则"
                form={form}
                initialValues={{
                    state: 1,
                }}
                modalProps={{
                    destroyOnClose: true,
                    onCancel: () => {
                        form.resetFields();
                    },
                }}
                open={createModalOpen}
                onOpenChange={handleModalOpen}
                onFinish={async (value) => {
                    const success = await handleAdd(value);
                    if (success) {
                        handleModalOpen(false);
                        if (actionRef.current) {
                            actionRef?.current.reload();
                        }
                    }
                }}
            >
                <ProFormText
                    hidden={true}
                    name="id"
                />
                <ProFormText
                    label="服务名称"
                    placeholder="请输入服务名称"
                    rules={[
                        {
                            required: true,
                            message: "请输入服务名称",
                        },
                    ]}
                    name="name"
                />
                <ProFormText
                    label="服务地址"
                    rules={[
                        {
                            required: true,
                            message: "请输入服务名称",
                        },
                    ]}
                    placeholder="请输入服务地址"
                    name="url"/>

                <ProFormSelect
                    label="服务状态"
                    placeholder="请输入服务状态"
                    rules={[
                        {
                            required: true,
                            message: "请输入服务状态",
                        },
                    ]}
                    options={[
                        {
                            label: '启用',
                            value: 1,
                        },
                        {
                            label: '禁用',
                            value: 0,
                        },
                    ]}
                    name="state"
                />
            </ModalForm>

        </PageContainer>
    );
};

export default NodePage;
