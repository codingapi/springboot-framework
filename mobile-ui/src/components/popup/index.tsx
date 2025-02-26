import React from "react";
import {Popup as AntPopup} from "antd-mobile";
import "./index.scss";

interface PopupProps {
    title?: string;
    visible: boolean;
    setVisible: (visible: boolean) => void;
    children?: React.ReactNode;
    position?: 'top' | 'right' | 'bottom' | 'left';
    bodyStyle?: React.CSSProperties;
    onOk?: () => void;
    onCancel?: () => void;
}

const Popup: React.FC<PopupProps> = (props) => {

    return (
        <AntPopup
            visible={props.visible}
            onMaskClick={() => {
                props.setVisible(false)
            }}
            onClose={() => {
                props.setVisible(false)
            }}
            position={props.position}
            bodyStyle={props.bodyStyle}
        >
            <div className={"mobile-popup"}>
                <div className={"mobile-popup-header"}>
                    <a
                        onClick={() => {
                            props.onCancel && props.onCancel();
                            props.setVisible(false)
                        }}
                    >取消</a>
                    <span>{props.title}</span>
                    <a
                        onClick={() => {
                            props.onOk && props.onOk();
                            props.setVisible(false)
                        }}
                    >确定</a>
                </div>
                <div className={"mobile-popup-content"}>
                    {props.children}
                </div>
            </div>

        </AntPopup>
    )
}

export default Popup;
