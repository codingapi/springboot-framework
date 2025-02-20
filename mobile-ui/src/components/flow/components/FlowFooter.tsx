import React, {useContext} from "react";
import {ActionSheet, Button} from "antd-mobile";
import {FlowViewReactContext} from "@/components/flow/view";

interface FlowFooterProps {
    maxButtonCount?: number;
}

const FlowFooter: React.FC<FlowFooterProps> = (props) => {
    const flowViewReactContext = useContext(FlowViewReactContext) || null;
    if (!flowViewReactContext) {
        return <></>;
    }

    const buttons = flowViewReactContext.getFlowButtons();
    const maxButtonCount = props.maxButtonCount || 4;

    const [visible, setVisible] = React.useState(false);

    return (
        <div className={"flow-view-footer"}>
            {buttons && buttons.length <= maxButtonCount && buttons.map((item) => {
                return (
                    <Button
                        key={item.id}
                        className={"flow-view-footer-button"}
                        style={{
                            ...item.style
                        }}
                    >{item.name}</Button>
                )
            })}
            {buttons && buttons.length > maxButtonCount && (
                <>
                    {buttons && buttons.slice(0, maxButtonCount - 1).map(item => {
                        return (
                            <Button
                                key={item.id}
                                className={"flow-view-footer-button"}
                                style={{
                                    ...item.style
                                }}
                            >{item.name}</Button>
                        )
                    })}

                    <Button
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
}

export default FlowFooter;
