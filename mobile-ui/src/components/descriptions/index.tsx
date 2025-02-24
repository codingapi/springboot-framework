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
}

// 详情展示
const Descriptions: React.FC<DescriptionsProps> = (props) => {

    const [data, setData] = React.useState<any>(null);

    const reload = () => {
        if (props.request) {
            props.request().then(data => {
                setData(data);
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
    }, []);

    return (
        <div className={"descriptions-list"}>
            {props.header}
            {data && props.columns && props.columns
                .filter(item => !item.props.hidden)
                .map((item) => {
                    const key = item.props.name;
                    const value = data[key] || null;
                    return (
                        <div className={"descriptions-list-item"}>
                            <div className={"descriptions-list-item-label"}
                                 dangerouslySetInnerHTML={{__html: item.props.label || ""}}/>
                            <div className={"descriptions-list-item-value"}>{value}</div>
                        </div>
                    )
                })}
            {props.footer}
        </div>
    )
}

export default Descriptions;
