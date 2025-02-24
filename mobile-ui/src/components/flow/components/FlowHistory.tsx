import React, {useContext} from "react";
import {FlowViewReactContext} from "@/components/flow/view";


const FlowHistory = () => {
    const flowViewReactContext = useContext(FlowViewReactContext);
    const flowRecordContext = flowViewReactContext?.flowRecordContext;

    const historyRecords = flowRecordContext?.getHistoryRecords();

    return (
        <div className={"flow-history"}>
            <div className={"flow-basic"}>

            </div>
            <div className={"flow-history-list"}>
                {historyRecords && historyRecords.map((item: any) => {
                    return (
                        <div>
                            <div>{item.title}</div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}

export default FlowHistory;
