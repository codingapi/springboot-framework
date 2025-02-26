import React from "react";
import Header from "@/layout/Header";
import FlowView from "@/components/flow/view";
import LeaveForm from "@/pages/levave/form";

const LeaveCreatePage = () => {

    const username = localStorage.getItem('username');

    return (
        <div>
            <Header>发起请假</Header>
            <FlowView
                view={LeaveForm}
                workCode={"leave"}
                formParams={{
                    days:1,
                    clazzName: 'com.codingapi.example.domain.Leave',
                    username: username
                }}
            />
        </div>
    )
}

export default LeaveCreatePage;
