import React from "react";
import {Provider} from "react-redux";
import {flowStore} from "@/components/flow/store/FlowSlice";
import {FlowFormViewProps} from "@/components/flow/types";


const $FlowView:React.FC<FlowFormViewProps> = (props)=>{

    return (
        <></>
    )
}

const FlowView:React.FC<FlowFormViewProps> = (props)=>{
    return (
        <Provider store={flowStore}>
            <$FlowView {...props} />
        </Provider>
    )
}

export default FlowView;
