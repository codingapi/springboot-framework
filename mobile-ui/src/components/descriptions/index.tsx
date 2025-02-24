import React, {useEffect, useImperativeHandle} from "react";
import {FormField} from "@/components/form/types";
import "./index.scss";

export interface DescriptionsAction {
    // 重新刷新数据
    reload: () => void;
}

// 详情展示属性
interface DescriptionsProps {
    // 展示的字段
    columns?: FormField[];
    // 请求数据
    request?: () => Promise<any>;
    // 操作对象
    actionRef?: React.Ref<DescriptionsAction>;
    // 页脚
    footer?: React.ReactNode;
    // 页头
    header?: React.ReactNode;
    // 数据转换
    dataConvert?: (field: FormField, data: any) => Promise<any>;
}

// 详情展示
const Descriptions: React.FC<DescriptionsProps> = (props) => {

    const [data, setData] = React.useState<any>(null);

    const reload = () => {
        if (props.request) {
            props.request().then((data) => {
                if(props.dataConvert) {
                    const promise = [] as Promise<any>[];
                    props.columns?.map(item => {
                        promise.push(new Promise<any>((resolve, reject) => {
                            props.dataConvert?.(item, data).then(value => {
                                data[item.props.name] = value;
                                resolve(value);
                            }).catch(reject);
                        }));
                    });
                    Promise.all(promise).then(() => {
                        setData(data);
                    });
                }else {
                    setData(data);
                }
            })
        }
    }

    useImperativeHandle(props.actionRef, () => ({
        reload: () => {
            reload()
        }
    }), [props.actionRef]);

    useEffect(() => {
        reload();

        return () => {
            setData(null);
        }
    }, []);

    return (
        <div className={"descriptions-list"}>
            {props.header}
            {data && props.columns && props.columns
                .filter(item => !item.props.hidden)
                .map((item) => {
                    const key = item.props.name;
                    const value = data[key];
                    const valueType = typeof value === 'object' ? 'object' : 'string';
                    return (
                        <div className={"descriptions-list-item"}>
                            <div className={"descriptions-list-item-label"}
                                 dangerouslySetInnerHTML={{__html: item.props.label || ""}}/>
                            {valueType === 'string' && (
                                <div className={"descriptions-list-item-value"}
                                     dangerouslySetInnerHTML={{__html: value}}/>
                            )}
                            {valueType === 'object' && (
                                <div className={"descriptions-list-item-value"}>
                                    {value}
                                </div>
                            )}
                        </div>
                    )
                })}
            {props.footer}
        </div>
    )
}

export default Descriptions;
