import React, {createContext, useEffect} from "react";
import {Provider, useDispatch, useSelector} from "react-redux";
import {FlowReduxState, flowStore, updateState} from "@/components/flow/store/FlowSlice";
import {FlowViewProps} from "@/components/flow/types";
import {Skeleton} from "antd-mobile";
import {FlowViewContext} from "@/components/flow/domain/FlowViewContext";
import {FlowEventContext} from "@/components/flow/domain/FlowEventContext";
import FlowFooter from "@/components/flow/components/FlowFooter";
import FlowContent from "@/components/flow/components/FlowContent";
import {detail} from "@/api/flow";
import {FormAction} from "@/components/form";
import {FlowStateContext} from "@/components/flow/domain/FlowStateContext";
import "./index.scss";

interface $FlowViewProps extends FlowViewProps {
    // 流程详情数据
    flowData: any;
}

interface FlowViewReactContextProps {
    flowViewContext: FlowViewContext;
    flowEventContext: FlowEventContext;
    flowStateContext: FlowStateContext;
    formAction: React.RefObject<FormAction>;
}

export const FlowViewReactContext = createContext<FlowViewReactContextProps | null>(null);

const $FlowView: React.FC<$FlowViewProps> = (props) => {

    const dispatch = useDispatch();

    const flowViewContext = new FlowViewContext(props, props.flowData);
    const formAction = React.useRef<FormAction>(null);
    const currentState = useSelector((state: FlowReduxState) => state.flow);

    const flowStateContext = new FlowStateContext(currentState, (state: any) => {
        dispatch(updateState({
            ...state
        }));
    });

    const flowEvenContext = new FlowEventContext(flowViewContext, formAction, flowStateContext);

    // 设置流程编号
    useEffect(() => {
        if (props.id) {
            flowStateContext.updateRecordId(props.id);
        }
    }, [props.id]);

    return (
        <FlowViewReactContext.Provider value={{
            flowViewContext: flowViewContext,
            flowEventContext: flowEvenContext,
            flowStateContext: flowStateContext,
            formAction: formAction,
        }}>
            <div className={"flow-view"}>
                <FlowContent/>
                <FlowFooter/>
            </div>
        </FlowViewReactContext.Provider>
    )
}

const FlowView: React.FC<FlowViewProps> = (props) => {
    const [data, setData] = React.useState<any>(null);

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

    return (
        <>
            {!data && (
                <>
                    <Skeleton.Title animated={true} className={"flow-skeleton-header"}/>
                    <Skeleton.Paragraph lineCount={5} animated={true} className={"flow-skeleton-body"}/>
                </>
            )}
            {data && (
                <Provider store={flowStore}>
                    <$FlowView
                        {...props}
                        flowData={data}
                    />
                </Provider>
            )}
        </>
    )
}

export default FlowView;
