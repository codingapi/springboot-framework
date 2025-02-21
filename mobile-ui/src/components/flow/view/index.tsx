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
import "./index.scss";
import FlowPage from "@/components/flow/components/FlowPage";


interface FlowViewReactContextProps {
    flowViewContext: FlowViewContext;
    flowEventContext: FlowEventContext;
    flowStateContext: FlowStateContext;
    formAction: React.RefObject<FormAction>;
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
                }
            });
        }
        if (props.workCode) {
            detail(null, props.workCode).then(res => {
                if (res.success) {
                    setData(res.data);
                }
            });
        }
    }

    useEffect(() => {
        loadFlowDetail();
    }, []);

    useEffect(() => {
        if (data) {
            dispatch(initState());
        }
    }, [data]);

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
