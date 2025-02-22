import React, {useContext} from "react";
import {ActionSheet, Button} from "antd-mobile";
import {FlowViewReactContext} from "@/components/flow/view";
import {useSelector} from "react-redux";
import {FlowReduxState} from "@/components/flow/store/FlowSlice";
import {useNavigate} from "react-router";

interface FlowFooterProps {
    maxButtonCount?: number;
}

const FlowFooter: React.FC<FlowFooterProps> = (props) => {
    const flowViewReactContext = useContext(FlowViewReactContext);

    const flowRecordContext = flowViewReactContext?.flowRecordContext;

    const flowButtonClickContext = flowViewReactContext?.flowButtonClickContext;

    const buttons = flowRecordContext?.getFlowButtons()||[];
    const maxButtonCount = props.maxButtonCount || 4;
    const requestLoading = useSelector((state: FlowReduxState) => state.flow.requestLoading);
    const contentHiddenVisible = useSelector((state: FlowReduxState) => state.flow.contentHiddenVisible);
    const [visible, setVisible] = React.useState(false);
    const navigate = useNavigate();

    const style = contentHiddenVisible ? {"display":"none"} : {};

    if(flowRecordContext?.isEditable()){
        return (
            <div className={"flow-view-footer"} style={style}>
                {buttons && buttons.length <= maxButtonCount && buttons.map((item) => {
                    const style = item.style && JSON.parse(item.style) || {};
                    return (
                        <Button
                            loading={requestLoading}
                            key={item.id}
                            className={"flow-view-footer-button"}
                            style={{
                                ...style
                            }}
                            onClick={() => {
                                flowButtonClickContext?.handlerClick(item);
                            }}
                        >{item.name}</Button>
                    )
                })}
                {buttons && buttons.length > maxButtonCount && (
                    <>
                        {buttons && buttons.slice(0, maxButtonCount - 1).map(item => {
                            const style = item.style && JSON.parse(item.style) || {};
                            return (
                                <Button
                                    loading={requestLoading}
                                    key={item.id}
                                    className={"flow-view-footer-button"}
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
                            loading={requestLoading}
                            color={"default"}
                            className={"flow-view-footer-button"}
                            onClick={() => {
                                setVisible(true);
                            }}
                        >
                            更多
                        </Button>

                        <ActionSheet
                            extra='请选择操作按钮'
                            cancelText='取消'
                            visible={visible}
                            actions={buttons.slice(maxButtonCount - 1).map((item, index) => {
                                return {
                                    text: item.name,
                                    key: item.id,
                                    onClick: () => {
                                        flowButtonClickContext?.handlerClick(item);
                                        setVisible(false);
                                    }
                                }
                            })}
                            onClose={() => setVisible(false)}
                        />
                    </>
                )}


            </div>
        )
    }else {
        return (
            <div className={"flow-view-footer"} style={style}>
                <Button
                    loading={requestLoading}
                    style={{
                        marginLeft:'15%',
                        marginRight:'15%'
                    }}
                    className={"flow-view-footer-button"}
                    onClick={() => {
                        navigate(-1);
                    }}
                >关闭</Button>
            </div>
        )
    }
}

export default FlowFooter;
