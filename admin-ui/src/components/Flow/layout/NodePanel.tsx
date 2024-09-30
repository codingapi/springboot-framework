import React from "react";
import {Button} from "antd";
import "./NodePanel.scss";

interface NodePanelProps{
    className:string
}

const NodePanel:React.FC<NodePanelProps> = (props)=>{

    return (
        <div className={props.className}>
            <h3>节点</h3>
            <div className={"node-content"}>
                <Button className={"node-item"}>开始节点</Button>
                <Button className={"node-item"}>流程节点</Button>
                <Button className={"node-item"}>结束节点</Button>
            </div>
        </div>
    )
}

export default NodePanel;
