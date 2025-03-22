import React from "react";

const componentBus = new Map<string, React.ComponentType<any>>();


// 注册表操作方法
export const registerComponent = (name: string, component: React.ComponentType<any>) => {
    componentBus.set(name, component);
};

// 获取组件
export const getComponent = (name: string): React.ComponentType<any> | undefined => {
    return componentBus.get(name);
}
