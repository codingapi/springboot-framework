import React, {useContext} from "react";
import {FlowViewReactContext} from "@/components/flow/view";


const FlowRecord = () => {
    const flowViewReactContext = useContext(FlowViewReactContext);
    const flowRecordContext = flowViewReactContext?.flowRecordContext;

    const historyRecords = flowRecordContext?.getHistoryRecords();

    return (
        <div>
            <div className={"flow-history-row-title"}>流程历史记录</div>

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

export default FlowRecord;
