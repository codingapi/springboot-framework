import React from "react";
import {AppleOutlined} from "@ant-design/icons";
import "./Control.scss";


interface ControlProps{
    className:string
}

const Control:React.FC<ControlProps> = (props)=>{

    return (
        <div className={props.className}>
            <div className={"control-content"}>
                <div><AppleOutlined/></div>
                <div><AppleOutlined/></div>
                <div><AppleOutlined/></div>
                <div><AppleOutlined/></div>
            </div>
        </div>
    )
}

export default Control;
