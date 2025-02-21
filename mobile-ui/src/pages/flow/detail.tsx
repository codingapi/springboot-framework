import React from "react";
import Header from "@/layout/Header";
import {useLocation} from "react-router";
import FlowView from "@/components/flow/view";
import {Result} from "antd-mobile";
import {flowViews} from "@/config/flows";


const FlowDetailPage = () => {

    const location = useLocation();
    const state = location.state;

    if (state) {
        return (
            <>
                <Header>{state.title}</Header>
                <FlowView
                    view={flowViews}
                    id={state.id}
                />
            </>
        )
    }

    return (
       <>
           <Header>流程详情</Header>
           <Result
               status={"error"}
               title={"页面参数错误"}
           />
       </>
    )
}

export default FlowDetailPage;
