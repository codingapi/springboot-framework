import React from "react";
import Header from "@/layout/Header";
import FlowView from "@/components/flow/view";
import LeaveForm from "@/pages/levave/form";

const LeaveCreatePage = () => {

    return (
        <div>
            <Header>发起请假</Header>

            <div
                style={{
                    marginTop: 5
                }}
            >
                <FlowView
                    view={LeaveForm}
                />
            </div>
        </div>
    )
}

export default LeaveCreatePage;
