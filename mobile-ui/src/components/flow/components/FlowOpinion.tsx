import React, {useContext} from "react";
import {FlowViewReactContext} from "@/components/flow/view";

const FlowOpinion = () => {

    const flowViewReactContext = useContext(FlowViewReactContext);
    const flowRecordContext = flowViewReactContext?.flowRecordContext;

    return (
        <>
            这里是流程的评论信息
        </>
    )
}

export default FlowOpinion;
