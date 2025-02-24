import React, {useEffect, useImperativeHandle} from "react";
import {FormField} from "@/components/form/types";
import "./index.scss";

interface DescriptionsAction {
    reload: () => void;
}

interface DescriptionsProps {
    columns?: FormField[];
    request?: () => Promise<any>;
    actionRef?: React.Ref<DescriptionsAction>;
}

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
            {data && props.columns && props.columns.filter(item => !item.props.hidden).map((item) => {
                const key = item.props.name;
                const value = data[key] || null;
                console.log('key:', key, 'value:', value);
                return (
                    <div className={"descriptions-list-item"}>
                        <div className={"descriptions-list-item-label"}>{item.props.label}</div>
                        <div className={"descriptions-list-item-value"}>{value}</div>
                    </div>
                )
            })}
        </div>
    )
}

export default Descriptions;
