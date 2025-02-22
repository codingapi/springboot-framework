import React, {useContext} from "react";
import {FlowViewReactContext} from "@/components/flow/view";


const FlowRecord = () => {
    const flowViewReactContext = useContext(FlowViewReactContext);

    return (
        <div>
            这里应该有流程的审批记录
        </div>
    )
}

export default FlowRecord;
