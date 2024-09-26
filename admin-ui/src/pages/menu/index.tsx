import React, {useEffect} from "react";
import {ModalForm, PageContainer, ProCard, ProForm, ProFormText} from "@ant-design/pro-components";
import {MenuRouteManager} from "@/framework/Routes/MenuRouteManager";
import {Popconfirm, Space, Tree} from "antd";
import {DeleteFilled, DownOutlined, EditFilled, PlusOutlined} from "@ant-design/icons";
import {useRoutesContext} from "@/framework/Routes/RoutesProvider";
import ProFormIcons from "@/components/Form/ProFormIcons";
import Index from "@/components/View/MenuIcon";
import "./index.scss";

const Menu = () => {

    const [root, setRoot] = React.useState<any>(null);

    const [editorForm] = ProForm.useForm();
    const [editorVisible, setEditorVisible] = React.useState(false);

    const [addForm] = ProForm.useForm();
    const [addVisible, setAddVisible] = React.useState(false);

    const {addMenu, removeMenu, updateMenu} = useRoutesContext();


    const handlerUpdate = (values: any) => {
        updateMenu(values);
        refreshTrees();
        setEditorVisible(false);
    }

    const handlerAdd = (values: any) => {
        addMenu(values);
        refreshTrees();
        setAddVisible(false);
    }

    const refreshTrees = () => {
        const fetchMenu = (item: any) => {
            if (item.icon) {
                item.iconKey = item.icon;
                item.icon = <Index icon={item.icon}/>
            }
            if (item.routes) {
                item.routes.map(fetchMenu);
                item.children = item.routes;
            }
            item.key = item.path;

            if (item.name) {
                item.title = (
                    <Space>
                        {item.name}

                        <PlusOutlined
                            onClick={() => {
                                addForm.setFieldsValue({parentPath: item.path});
                                setAddVisible(true);
                            }}
                        />

                        <EditFilled
                            onClick={() => {
                                console.log(item);
                                editorForm.setFieldsValue(item);
                                editorForm.setFieldValue('icon', item.iconKey);
                                setEditorVisible(true);
                            }}
                        />

                        <Popconfirm title={"确认删除吗?"} onConfirm={() => {
                            removeMenu(item.path);
                            refreshTrees();
                        }}>
                            <DeleteFilled/>
                        </Popconfirm>
                    </Space>
                );
            }

            return item;
        }

        const menus = MenuRouteManager.getInstance().getMenus(false);
        menus.map(fetchMenu);

        setRoot({
            title: (
                <Space>
                    所有菜单

                    <PlusOutlined
                        onClick={() => {
                            addForm.setFieldsValue({parentPath: ""});
                            setAddVisible(true);
                        }}
                    />

                </Space>
            ),
            key: 'root',
            children: menus
        });
    };

    useEffect(() => {
        refreshTrees();
    }, []);

    return (
        <PageContainer>
            <ProCard
                colSpan={8}
                title={"菜单列表"}
            >
                {root && (
                    <Tree
                        showIcon
                        selectable={false}
                        defaultExpandAll
                        switcherIcon={<DownOutlined/>}
                        treeData={[root]}
                    />
                )}

            </ProCard>

            <ModalForm
                form={editorForm}
                open={editorVisible}
                title={"新增菜单"}
                modalProps={{
                    onCancel: () => {
                        setEditorVisible(false);
                    }
                }}
                onFinish={async (values) => {
                    updateMenu(values);
                    return true;
                }}
            >
                <ProFormText
                    name={"name"}
                    label={"名称"}
                    rules={[
                        {
                            required: true,
                            message: '名称是必填的'
                        }
                    ]}
                />

                <ProFormIcons
                    name={"icon"}
                    label={"图标"}
                />

                <ProFormText
                    name={"path"}
                    label={"路径"}
                    rules={[
                        {
                            required: true,
                            message: '路径是必填的'
                        }
                    ]}
                />

            </ModalForm>


            <ModalForm
                form={editorForm}
                open={editorVisible}
                title={"编辑菜单"}
                modalProps={{
                    onCancel: () => {
                        setEditorVisible(false);
                    }
                }}
                onFinish={async (values) => {
                    console.log(values);
                    handlerUpdate(values);
                    return true;
                }}
            >
                <ProFormText
                    name={"name"}
                    label={"名称"}
                    rules={[
                        {
                            required: true,
                            message: '名称是必填的'
                        }
                    ]}
                />

                <ProFormIcons
                    name={"icon"}
                    label={"图标"}
                />

                <ProFormText
                    name={"path"}
                    label={"路径"}
                    rules={[
                        {
                            required: true,
                            message: '路径是必填的'
                        }
                    ]}
                />

                <ProFormText
                    name={"page"}
                    label={"界面地址"}
                />

            </ModalForm>

            <ModalForm
                form={addForm}
                open={addVisible}
                title={"添加菜单"}
                modalProps={{
                    onCancel: () => {
                        setAddVisible(false);
                    }
                }}
                onFinish={async (values) => {
                    handlerAdd(values);
                    return true;
                }}
            >
                <ProFormText
                    name={"parentPath"}
                    hidden={true}
                />

                <ProFormText
                    name={"name"}
                    label={"名称"}
                    rules={[
                        {
                            required: true,
                            message: '名称是必填的'
                        }
                    ]}
                />

                <ProFormIcons
                    name={"icon"}
                    label={"图标"}
                />

                <ProFormText
                    name={"path"}
                    label={"路径"}
                    rules={[
                        {
                            required: true,
                            message: '路径是必填的'
                        }
                    ]}
                />

                <ProFormText
                    name={"page"}
                    label={"界面地址"}
                />

            </ModalForm>

        </PageContainer>
    )
}

export default Menu;