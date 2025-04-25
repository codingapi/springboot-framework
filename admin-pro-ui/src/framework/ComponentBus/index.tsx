import React from "react";
import {loadRemoteComponent, loadRemoteScript} from "@/utils/dynamicLoader";

class ComponentBus {

    private readonly componentBus = new Map<string, React.ComponentType<any>>();

    /**
     * 注册组件
     * @param key 组件key
     * @param component 组件类型
     */
    public registerComponent = (key: string, component: React.ComponentType<any>) => {
        this.componentBus.set(key, component);
    }

    /**
     * 删除组件
     * @param key  组件key
     */
    public removeComponent = (key: string) => {
        this.componentBus.delete(key);
    }

    /**
     * 注册远程组件
     * @param key 组件key
     * @param remoteUrl 远程组件地址
     * @param scope     远程组件作用域
     * @param module    远程组件名称
     */
    public registerRemoteComponent = (key: string, remoteUrl: string, scope: string, module: string): Promise<boolean> => {
        return new Promise((resolve, reject) => {
            loadRemoteScript(remoteUrl).then(() => {
                loadRemoteComponent(scope, module)
                    .then((ComponentModule: any) => {
                        const Component = ComponentModule.default || ComponentModule;
                        this.registerComponent(key, Component);
                        resolve(true);
                    }).catch(error => {
                    reject(error);
                });
            }).catch(error => {
                reject(error);
            });
        })
    }

    /**
     * 获取组件
     * @param key 组件key
     * @param defaultComponent 默认组件
     */
    public getComponent = <T extends {} = any>(key: string, defaultComponent?: React.ComponentType<T>): React.ComponentType<T> | undefined => {
        const component = this.componentBus.get(key) as React.ComponentType<T> | undefined;
        if (component) {
            return component;
        }
        if (defaultComponent) {
            return defaultComponent;
        }
        return undefined;
    };

    private ComponentBus() {
        // 私有构造函数，防止外部实例化
    }

    private static instance: ComponentBus = new ComponentBus();

    // 单例模式
    public static getInstance(): ComponentBus {
        return ComponentBus.instance;
    }
}

export default ComponentBus;

