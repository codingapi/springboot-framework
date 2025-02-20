import React, {useContext} from "react";
import {FlowViewReactContext} from "@/components/flow/view";


const FlowHistory = () => {
    const flowViewReactContext = useContext(FlowViewReactContext) || null;
    if (!flowViewReactContext) {
        return <></>;
    }

    return (
        <></>
    )
}

export default FlowHistory;
