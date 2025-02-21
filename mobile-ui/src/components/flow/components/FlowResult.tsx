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
            status={result?.state}
            title={result?.title}
            description={(
                <div className={"flow-result-content"}>
                    {result && result.items && result.items.map((item)=>{
                        console.log(item);
                        return (
                            <div className={"flow-result-content-item"}>
                                <div className={"flow-result-content-item-label"}>{item.label}:</div>
                                <div className={"flow-result-content-item-value"}>{item.value}</div>
                            </div>
                        )
                    })}

                    {result?.closeable && (
                        <div className={"flow-result-content-footer"}>
                            <Button
                                className={"flow-result-content-button"}
                                block={true}
                                onClick={()=>{
                                    navigate(-1);
                                }}
                            >关闭页面</Button>
                        </div>
                    )}
                </div>
            )}
        />
    )
}

export default FlowResult;
