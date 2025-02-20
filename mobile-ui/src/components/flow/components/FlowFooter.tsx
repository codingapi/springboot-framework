import React from "react";
import {ActionSheet, Button} from "antd-mobile";


interface FlowButton {
    title: string;
    color: string;
    onClick: () => void;
}

interface FlowFooterProps {
    maxButtonCount?: number;
    buttons?: FlowButton[];
}

const FlowFooter: React.FC<FlowFooterProps> = (props) => {
    const buttons = props.buttons || [];
    const maxButtonCount = props.maxButtonCount || 4;

    const [visible, setVisible] = React.useState(false);

    return (
        <div className={"flow-view-footer"}>
            {buttons && buttons.length <= maxButtonCount && buttons.map((item) => {
                return (
                    <Button
                        color={item.color as any}
                        className={"flow-view-footer-button"}
                    >{item.title}</Button>
                )
            })}
            {buttons && buttons.length > maxButtonCount && (
                <>
                    {buttons && buttons.slice(0, maxButtonCount - 1).map(item => {
                        return (
                            <Button
                                color={item.color as any}
                                className={"flow-view-footer-button"}
                            >{item.title}</Button>
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
                                text: item.title,
                                key: index,
                                onClick: () => {
                                    item.onClick();
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
