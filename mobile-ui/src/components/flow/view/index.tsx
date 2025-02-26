import React, {createContext, useEffect} from "react";
import {Provider, useDispatch, useSelector} from "react-redux";
import {FlowReduxState, flowStore, initState, updateState} from "@/components/flow/store/FlowSlice";
import {FlowViewProps} from "@/components/flow/types";
import {Skeleton} from "antd-mobile";
import {FlowRecordContext} from "@/components/flow/domain/FlowRecordContext";
import {FlowEventContext} from "@/components/flow/domain/FlowEventContext";
import {detail} from "@/api/flow";
import {FormAction} from "@/components/form";
import {FlowStateContext} from "@/components/flow/domain/FlowStateContext";
import FlowPage from "@/components/flow/components/FlowPage";
import {FlowTriggerContext} from "@/components/flow/domain/FlowTriggerContext";
import {FlowButtonClickContext} from "@/components/flow/domain/FlowButtonClickContext";
import "./index.scss";

// 流程视图上下文属性
interface FlowViewReactContextProps {
    // 流程的详情控制上下文对象
    flowRecordContext: FlowRecordContext;
    // 流程的事件控制上下文对象
    flowEventContext: FlowEventContext;
    // 流程的状态数据上下文对象
    flowStateContext: FlowStateContext;
    // 流程事件触发控制上下文对象
    flowTriggerContext: FlowTriggerContext;
    // 流程按钮点击触发控制器上下文对象
    flowButtonClickContext: FlowButtonClickContext;
    // 表单操作对象
    formAction: React.RefObject<FormAction>;
    // 审批意见操作对象
    opinionAction: React.RefObject<FormAction>;
}

export const FlowViewReactContext = createContext<FlowViewReactContextProps | null>(null);

const $FlowView: React.FC<FlowViewProps> = (props) => {
    const [data, setData] = React.useState<any>(null);

    const dispatch = useDispatch();

    const version = useSelector((state: FlowReduxState) => state.flow.version);

    // 请求流程详情数据
    const loadFlowDetail = () => {
        if (props.id) {
            detail(props.id, null).then(res => {
                if (res.success) {
                    setData(res.data);
                }
            });
            return;
        }
        if (props.workCode) {
            detail(null, props.workCode).then(res => {
                if (res.success) {
                    setData(res.data);
                }
            });
            return;
        }
    }

    useEffect(() => {
        if(data){
            const dataVersion = Math.random();
            dispatch(updateState({dataVersion:dataVersion}));
        }
    }, [data]);

    useEffect(() => {
        return ()=>{
            dispatch(initState());
        }
    }, []);

    useEffect(() => {
        loadFlowDetail();
    }, [version]);


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
