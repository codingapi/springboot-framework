import React, {useEffect} from "react";
import {FlowFormViewProps, FlowViewProps} from "@/components/flow/types";
import {useDispatch, useSelector} from "react-redux";
import {FlowReduxState, updateState} from "@/components/flow/store/FlowSlice";
import {FlowViewContext} from "@/components/flow/domain/FlowViewContext";
import {FormAction} from "@/components/form";
import {FlowStateContext} from "@/components/flow/domain/FlowStateContext";
import {FlowEventContext} from "@/components/flow/domain/FlowEventContext";
import FlowResult from "@/components/flow/components/FlowResult";
import FlowContent from "@/components/flow/components/FlowContent";
import FlowFooter from "@/components/flow/components/FlowFooter";
import {FlowViewReactContext} from "@/components/flow/view";
import FlowForm404 from "@/components/flow/components/FlowForm404";


interface FlowPageProps extends FlowViewProps {
    // 流程详情数据
    flowData: any;
}

const FlowPage: React.FC<FlowPageProps> = (props) => {

    const dispatch = useDispatch();

    const currentState = useSelector((state: FlowReduxState) => state.flow);
    const flowViewContext = new FlowViewContext(props, props.flowData);
    const formAction = React.useRef<FormAction>(null);
    const result = currentState.result;

    console.log('currentState', currentState);

    const flowStateContext = new FlowStateContext(currentState, (state: any) => {
        dispatch(updateState({
            ...state
        }));
    });

    const flowEvenContext = new FlowEventContext(flowViewContext, formAction, flowStateContext);
    const FlowFormView = flowViewContext.getFlowFormView() as React.ComponentType<FlowFormViewProps>;

    // 设置流程编号
    useEffect(() => {
        if (props.id) {
            flowStateContext.setRecordId(props.id);
        }
    }, [props.id]);

    if(FlowFormView) {
        return (
            <FlowViewReactContext.Provider value={{
                flowViewContext: flowViewContext,
                flowEventContext: flowEvenContext,
                flowStateContext: flowStateContext,
                formAction: formAction,
            }}>
                <div className={"flow-view"}>
                    {result && (
                        <FlowResult/>
                    )}
                    {!result && (
                        <>
                            <FlowContent/>
                            <FlowFooter/>
                        </>
                    )}
                </div>
            </FlowViewReactContext.Provider>
        )
    }else {
        return (
            <FlowForm404/>
        )
    }
}

export default FlowPage;
