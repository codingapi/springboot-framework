import {FlowViewProps} from "@/components/flow/types";
import React from "react";
import {Modal} from "antd";
import FlowView from "@/components/flow/view/index";
import "./FlowModelView.scss";

 const FlowModelView:React.FC<FlowViewProps> = (props)=>{
    return (
        <Modal
            className={"flow-modal"}
            open={props.visible}
            onCancel={()=>{
                props.setVisible(false);
            }}
            onOk={()=>{
                props.setVisible(false);
            }}
            onClose={()=>{
                props.setVisible(false);
            }}
            destroyOnClose={true}
            footer={false}
            closable={false}
        >
            <FlowView
                {...props}
            />
        </Modal>
    )
}

export default FlowModelView;
