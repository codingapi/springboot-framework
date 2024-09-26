import React, {Suspense, useState} from "react";
import {Button, Col, Input, Row, Select, Space, Spin} from "antd";
import ProFormCode from "@/components/Form/ProFormCode";
import {PageContainer} from "@ant-design/pro-components";
import loadComponent from "@/framework/DynamicLoad/DynamicCode";
import {useRoutesContext} from "@/framework/Routes/RoutesProvider";

const Test1 = () => {

    const [RemoteTestComponent, setRemoteTestComponent] = useState<React.ComponentType<any> | null>(null);
    const {addMenu} = useRoutesContext();

    const defaultCode =
        `
    const Test = () => {
        return (
            <div>
                <h1>Test</h1>
                <Button onClick={() => alert('Hello!')}>Click Me</Button>
                <Input placeholder="Enter text" />
                <Select defaultValue="option1" style={{ width: 120 }}>
                    <Select.Option value="option1">Option 1</Select.Option>
                    <Select.Option value="option2">Option 2</Select.Option>
                </Select>
            </div>
        );
    };
    callback(Test);
`;
    const [code, setCode] = useState(defaultCode); // 直接在代码中返回 Test 组件实例

    const executeCode = (codeStr: string, mode: string) => {
        loadComponent(codeStr, [
            Button,
            Input,
            Select
        ]).then((Component) => {

            setRemoteTestComponent(() => Component);

            if (mode === 'menu') {
                addMenu({
                    path: '/test',
                    name: '测试页面',
                    icon: 'BugFilled',
                    routes: [
                        {
                            path: '/test/test1',
                            element: (
                                <PageContainer>
                                    <Component/>
                                </PageContainer>
                            ),
                            name: '测试页面1',
                        }
                    ]
                });
            }
        }).catch((error) => {
            console.error(error);
        });
    };


    return (
        <PageContainer
            title={"动态加载代码"}
            extra={(
                <Space>

                    <Button
                        type={"primary"}
                        onClick={() => {
                            executeCode(code, 'load');
                        }}
                    >
                        Run Code
                    </Button>

                    <Button
                        onClick={() => {
                            executeCode(code, 'menu');
                        }}
                    >
                        Add Menu
                    </Button>


                </Space>

            )}
        >
            <Row>
                <Col span={12}>
                    <ProFormCode
                        value={code}
                        style={{
                            height: 500,
                        }}
                        language={"javascript"}
                        onChange={(value) => {
                            setCode(value);
                        }}
                    />
                </Col>
                <Col span={12}>
                    {RemoteTestComponent && (
                        <Suspense fallback={<Spin tip={"Loading"} size={"large"}/>}>
                            <RemoteTestComponent/>
                        </Suspense>
                    )}
                </Col>
            </Row>
        </PageContainer>
    );
};

export default Test1;