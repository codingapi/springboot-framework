import React from "react";
import {Button, Result} from "antd";

interface FlowForm404Props{
    setVisible:(visible:boolean)=>void;
}

const FlowForm404:React.FC<FlowForm404Props> = (props)=>{

    return (
        <Result
            status='error'
            title={"抱歉，没有配置流程视图"}
        >
            <Button
                className={"flow-result-content-button"}
                block={true}
                onClick={()=>{
                    props.setVisible(false);
                }}
            >关闭页面</Button>
        </Result>
    )
}

export default FlowForm404;
