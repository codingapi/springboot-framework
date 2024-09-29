import React from "react";
import {Button} from "antd";
import "./NodePanel.scss";

interface NodePanelProps{
    className:string
}

const NodePanel:React.FC<NodePanelProps> = (props)=>{

    return (
        <div className={props.className}>
            <h3>Nodes</h3>
            <div className={"node-content"}>
                <Button>Node1</Button>
            </div>
            <div className={"node-content"}>
                <Button>Node2</Button>
            </div>
            <div className={"node-content"}>
                <Button>Node3</Button>
            </div>
        </div>
    )
}

export default NodePanel;
