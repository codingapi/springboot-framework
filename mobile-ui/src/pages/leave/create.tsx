import React from "react";
import Header from "@/layout/Header";
import {FlowView} from "@codingapi/flow-mobile";
import LeaveForm from "@/pages/leave/form";
import {useNavigate} from "react-router";

const LeaveCreatePage = () => {

    const username = localStorage.getItem('username');
    const navigate = useNavigate();

    return (
        <div>
            <Header>发起请假</Header>
            <FlowView
                view={LeaveForm}
                workCode={"leave"}
                setVisible={()=>{
                    navigate(-1);
                }}
                visible={true}
                formParams={{
                    days:1,
                    clazzName: 'com.codingapi.example.infra.flow.form.LeaveForm',
                    username: username
                }}
            />
        </div>
    )
}

export default LeaveCreatePage;
