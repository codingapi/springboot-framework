import React from "react";
import {Button, Result} from "antd-mobile";
import {useNavigate} from "react-router";


const FlowForm404 = ()=>{

    const navigate = useNavigate();

    return (
        <Result
            status='error'
            title={"抱歉，没有配置流程视图"}
            description={(
                <div className={"flow-result-content"}>
                    <Button
                        className={"flow-result-content-button"}
                        block={true}
                        onClick={()=>{
                            navigate(-1);
                        }}
                    >关闭页面</Button>
                </div>
            )}
        />
    )
}

export default FlowForm404;
