import React, {createContext, useEffect} from "react";
import {Provider} from "react-redux";
import {flowStore} from "@/components/flow/store/FlowSlice";
import {FlowViewProps} from "@/components/flow/types";
import {Skeleton} from "antd-mobile";
import {FlowViewContext} from "@/components/flow/domain/FlowViewContext";
import {FlowEventContext} from "@/components/flow/domain/FlowEventContext";
import FlowFooter from "@/components/flow/components/FlowFooter";
import FlowContent from "@/components/flow/components/FlowContent";
import {detail} from "@/api/flow";
import {FormAction} from "@/components/form";
import "./index.scss";

interface $FlowViewProps extends FlowViewProps {
    // 流程详情数据
    flowData: any;
}

interface FlowViewReactContextProps {
    flowViewContext: FlowViewContext;
    formAction: React.RefObject<FormAction>;
    flowEventContext: FlowEventContext;
}

export const FlowViewReactContext = createContext<FlowViewReactContextProps | null>(null);

const $FlowView: React.FC<$FlowViewProps> = (props) => {

    const flowViewContext = new FlowViewContext(props, props.flowData);
    const formAction = React.useRef<FormAction>(null);
    const flowEvenContext = new FlowEventContext(flowViewContext, formAction);

    return (
        <FlowViewReactContext.Provider value={{
            flowViewContext: flowViewContext,
            formAction: formAction,
            flowEventContext: flowEvenContext
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
