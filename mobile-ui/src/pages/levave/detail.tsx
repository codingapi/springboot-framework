import React from "react";
import Header from "@/layout/Header";
import {useLocation} from "react-router";
import Descriptions from "@/components/descriptions";
import {fields} from "@/pages/levave/fields";

const LeaveDetailPage = () => {

    const location = useLocation();
    const state = location.state;

    return (
        <>
            <Header>流程详情</Header>
            <Descriptions
                columns={fields}
                request={async ()=>{
                    return state;
                }}
            />
        </>
    )
}

export default LeaveDetailPage;
