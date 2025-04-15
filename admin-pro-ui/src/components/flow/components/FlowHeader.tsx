import React, {useContext} from "react";
import {FlowViewReactContext} from "@/components/flow/view";
import {useSelector} from "react-redux";
import {FlowReduxState} from "@/components/flow/store/FlowSlice";
import {Button} from "antd";

interface FlowHeaderProps{
    setVisible:(visible:boolean)=>void;
}

const FlowHeader:React.FC<FlowHeaderProps> = (props) => {
    const flowViewReactContext = useContext(FlowViewReactContext);

    const flowRecordContext = flowViewReactContext?.flowRecordContext;

    const flowButtonClickContext = flowViewReactContext?.flowButtonClickContext;

    const buttons = flowRecordContext?.getFlowButtons()||[];
    const requestLoading = useSelector((state: FlowReduxState) => state.flow.requestLoading);
    const contentHiddenVisible = useSelector((state: FlowReduxState) => state.flow.contentHiddenVisible);

    const style = contentHiddenVisible ? {"display":"none"} : {};

    if(flowRecordContext?.isEditable()){
        return (
            <div className={"flow-view-header"} style={style}>
                {buttons.map((item) => {
                    const style = item.style && JSON.parse(item.style) || {};
                    return (
                        <Button
                            loading={requestLoading}
                            key={item.id}
                            className={"flow-view-header-button"}
                            style={{
                                ...style
                            }}
                            onClick={() => {
                                flowButtonClickContext?.handlerClick(item);
                            }}
                        >{item.name}</Button>
                    )
                })}

                <Button
                    color={"default"}
                    className={"flow-view-header-button"}
                    onClick={() => {
                        props.setVisible(false);
                    }}
                >
                    关闭
                </Button>

            </div>
        )
    }else {
        return (
            <div className={"flow-view-header"} style={style}>
                <Button
                    loading={requestLoading}
                    style={{
                        marginLeft:'15%',
                        marginRight:'15%'
                    }}
                    className={"flow-view-header-button"}
                    onClick={() => {
                        props.setVisible(false);
                    }}
                >关闭</Button>
            </div>
        )
    }
}

export default FlowHeader;
