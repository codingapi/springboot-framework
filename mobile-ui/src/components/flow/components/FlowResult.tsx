import React from "react";
import {Button, Result} from "antd-mobile";
import {useSelector} from "react-redux";
import {FlowReduxState} from "@/components/flow/store/FlowSlice";
import {useNavigate} from "react-router";


const FlowResult = ()=>{

    const result = useSelector((state: FlowReduxState) => state.flow.result);
    const navigate = useNavigate();

    return (
        <Result
            status='success'
            title={result?.title}
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

export default FlowResult;
