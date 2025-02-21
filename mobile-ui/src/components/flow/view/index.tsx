import React, {createContext, useEffect} from "react";
import {Provider, useDispatch} from "react-redux";
import {flowStore, initState} from "@/components/flow/store/FlowSlice";
import {FlowViewProps} from "@/components/flow/types";
import {Skeleton} from "antd-mobile";
import {FlowViewContext} from "@/components/flow/domain/FlowViewContext";
import {FlowEventContext} from "@/components/flow/domain/FlowEventContext";
import {detail} from "@/api/flow";
import {FormAction} from "@/components/form";
import {FlowStateContext} from "@/components/flow/domain/FlowStateContext";
import FlowPage from "@/components/flow/components/FlowPage";
import "./index.scss";

// 流程视图上下文属性
interface FlowViewReactContextProps {
    // 流程的详情控制上下文对象
    flowViewContext: FlowViewContext;
    // 流程的事件控制上下文对象
    flowEventContext: FlowEventContext;
    // 流程的状态数据上下文对象
    flowStateContext: FlowStateContext;
    // 表单操作对象
    formAction: React.RefObject<FormAction>;
    // 审批意见操作对象
    opinionAction: React.RefObject<FormAction>;
}

export const FlowViewReactContext = createContext<FlowViewReactContextProps | null>(null);

const $FlowView: React.FC<FlowViewProps> = (props) => {
    const [data, setData] = React.useState<any>(null);

    const dispatch = useDispatch();

    // 请求流程详情数据
    const loadFlowDetail = () => {
        if (props.id) {
            detail(props.id, null).then(res => {
                if (res.success) {
                    setData(res.data);
                    dispatch(initState());
                }
            });
        }
        if (props.workCode) {
            detail(null, props.workCode).then(res => {
                if (res.success) {
                    setData(res.data);
                    dispatch(initState());
                }
            });
        }
    }

    useEffect(() => {
        loadFlowDetail();
    }, []);


    return (
        <>
            {!data && (
                <>
                    <Skeleton.Title animated={true} className={"flow-skeleton-header"}/>
                    <Skeleton.Paragraph lineCount={5} animated={true} className={"flow-skeleton-body"}/>
                </>
            )}
            {data && (
                <FlowPage
                    {...props}
                    flowData={data}
                />
            )}
        </>
    )
}

const FlowView: React.FC<FlowViewProps> = (props) => {
    return (
        <Provider store={flowStore}>
            <$FlowView  {...props} />
        </Provider>
    )
}

export default FlowView;
