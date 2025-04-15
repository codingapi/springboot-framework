import React, {useContext} from "react";
import {FlowViewReactContext} from "@/components/flow/view";
import moment from "moment";


const optionStateConvert = (data: any) => {
    if (data.opinion.result === 1) {
        return '转办'
    }
    if (data.opinion.result === 2) {
        return '通过'
    }
    if (data.opinion.result === 3) {
        return '驳回'
    }
    return '暂存'
}

const FlowOpinion = () => {

    const flowViewReactContext = useContext(FlowViewReactContext);
    const flowRecordContext = flowViewReactContext?.flowRecordContext;

    const historyOpinions = flowRecordContext?.getHistoryOpinions();

    return (
        <>
            {historyOpinions && historyOpinions.map((item: any) => {
                const flowOperatorName = item.operator.name;
                const optionState = optionStateConvert(item);
                const createTime = moment(item.createTime).format("YYYY-MM-DD HH:mm:ss");
                const advice = item.opinion.advice;
                return (
                    <div className={"flow-history-opinion-item"}>
                        <div className={"flow-history-opinion-item-row"}>
                            <span>审批人：</span>
                            <span>{flowOperatorName} {optionState}</span>
                        </div>
                        <div className={"flow-history-opinion-item-row"}>
                            <span>审批时间：</span>
                            <span>{createTime}</span>
                        </div>
                        <div className={"flow-history-opinion-item-row"}>
                            <span>审批意见：</span>
                            <span>{advice}</span>
                        </div>
                    </div>
                )
            })}
        </>
    )
}

export default FlowOpinion;
