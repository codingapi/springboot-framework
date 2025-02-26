import * as Babel from "@babel/standalone";
import React from "react";
import ReactDOM from "react-dom/client";

const loadComponent = (code: string, importComponents: React.ComponentType<any>[]):Promise<any> => {
    return new Promise((resolve, reject) => {
        try {
            // 使用 Babel 编译包含 JSX 的代码
            const compiledCode = Babel.transform(code, {
                presets: ["react"]
            }).code;

            // 回调函数，返回组件
            const callback = (Component: any) => {
                resolve(Component);
            }

            // 依赖的组件的对象
            const function_components = [
                React,
                ReactDOM,
                callback,
                ...importComponents,
            ];

            // 依赖的组件的名称
            const function_componentsName = [
                "React",
                "ReactDOM",
                "callback",
                ...importComponents.map((component:any) =>{
                    return component.displayName
                }),
            ]
            // 使用 new Function 执行编译后的代码
            // eslint-disable-next-line no-new-func
            const renderComponent = new Function(...function_componentsName, compiledCode as string);
            // 运行生成的代码
            renderComponent(...function_components);
        } catch (error) {
            reject(error);
        }
    });
};

export default loadComponent;
