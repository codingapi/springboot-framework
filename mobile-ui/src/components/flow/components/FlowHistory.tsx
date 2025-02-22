import React, {useContext} from "react";
import {FlowViewReactContext} from "@/components/flow/view";
import FlowChart from "@/components/flow/components/FlowChart";
import FlowRecord from "@/components/flow/components/FlowRecord";


const FlowHistory = () => {
    const flowViewReactContext = useContext(FlowViewReactContext);

    return (
        <div>
            这里是流程的历史信息
            <FlowChart/>
            <FlowRecord/>
        </div>
    )
}

export default FlowHistory;
