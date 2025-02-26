import React, {Suspense, useEffect, useState} from "react";
import {Button, Space, Spin} from "antd";
import {ModalForm, PageContainer, ProForm, ProFormText} from "@ant-design/pro-components";
import ProFormUploader from "@/components/Form/ProFormUploader";
import {loadRemoteComponent, loadZipJsFileScript} from "@/utils/dynamicLoader";
import {useRoutesContext} from "@/framework/Routes/RoutesProvider";
import Role from "@/pages/role";


const Test2 = () => {
    const [RemoteTestComponent, setRemoteTestComponent] = useState<React.ComponentType<any> | null>(null);

    const {addMenu} = useRoutesContext();

    const [visible, setVisible] = useState(false);
    const [mode, setMode] = useState('zip' as 'zip' | 'menu');

    const [form] = ProForm.useForm();

    useEffect(() => {
        if(visible){
            form.setFieldsValue({
                scope: "MircoApp",
                module: "./Header",
            })
        }
    }, [visible]);

    const loadComponent = (values: any) => {
        return new Promise((resolve, reject) => {
            const base64 = values.component;
            const scope = values.scope;
            const module = values.module;
            loadZipJsFileScript(base64).then(() => {
                loadRemoteComponent(scope, module).then((ComponentModule: any) => {
                    const Component = ComponentModule.default || ComponentModule;
                    resolve(Component);
                }).catch(e => {
                    reject(e);
                });
            });
        });
    }

    const handlerLoadComponent = async (values: any) => {
        loadComponent(values).then((Component:any) => {
            if(Component) {
                if (mode === 'menu') {
                   addMenu({
                        path: '/test',
                        name:'测试页面',
                        icon: 'BugFilled',
                        routes:[
                            {
                                path: '/test/test1',
                                element: (
                                    <PageContainer>
                                        <Component/>
                                    </PageContainer>
                                ),
                                name:'测试页面1',
                            }
                        ]
                    });
                }else{
                    setRemoteTestComponent(() => Component);
                }
            }
            setVisible(false);
        });
    }

    return (
        <PageContainer>
            <div
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    flexDirection: 'column',
                    gap: '50px',
                }}
            >
                <h1
                    style={{
                        textAlign: 'center'
                    }}
                > 动态加载组件 </h1>

                {RemoteTestComponent && (
                    <Suspense fallback={<Spin tip={"Loading"} size={"large"}/>}>
                        <RemoteTestComponent
                            title={"Remote Component Header"}
                            onClick={() => {
                                alert('click');
                            }}
                        />
                    </Suspense>
                )}
                <Space>
                    <Button
                        onClick={() => {
                            setMode('zip');
                            setVisible(true);
                        }}
                    >
                        上传zip组件到本界面
                    </Button>

                    <Button
                        onClick={() => {
                            setMode('menu');
                            setVisible(true);
                        }}
                    >
                        上传zip组件到菜单
                    </Button>

                    <Button
                        onClick={() => {
                            addMenu({
                                path: '/role',
                                name:'权限管理',
                                icon: 'BugFilled',
                                routes:[
                                    {
                                        path: '/role/index',
                                        element: (
                                            <Role/>
                                        ),
                                        name:'权限测试',
                                    }
                                ]
                            });
                        }}
                    >
                        上传权限组件到菜单
                    </Button>

                </Space>


                <ModalForm
                    form={form}
                    title={"上传zip组件"}
                    open={visible}
                    modalProps={{
                        onCancel: () => {
                            setVisible(false);
                        },
                        destroyOnClose: true
                    }}
                    onFinish={handlerLoadComponent}
                >
                    <ProFormText
                        name={"component"}
                        hidden={true}
                    />

                    <ProFormText
                        name={"scope"}
                        label={"scope"}
                        rules={[
                            {
                                required: true,
                                message: "scope is required"
                            }
                        ]}
                    />

                    <ProFormText
                        name={"module"}
                        label={"module"}
                        rules={[
                            {
                                required: true,
                                message: "module is required"
                            }
                        ]}
                    />

                    <ProFormUploader
                        label={"组件文件包"}
                        name={"upload"}
                        max={1}
                        accept={".zip"}
                        onChange={({file}) => {
                            if (file.response) {
                                form.setFieldValue('component', file.response);
                            }
                        }}
                        rules={[
                            {
                                required: true,
                                message: "upload zip file"
                            }
                        ]}
                    />

                </ModalForm>
            </div>
        </PageContainer>
    )
}

export default Test2;
