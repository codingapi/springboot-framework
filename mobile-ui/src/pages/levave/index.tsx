import React, {useEffect} from "react";
import Header from "@/layout/Header";
import {list} from "@/api/leave";


const LeaveListPage = ()=>{

    useEffect(() => {
        list().then(res=>{
            console.log(res);
        })
    }, []);

    return (
        <>
            <Header>请假历史</Header>
        </>
    )
}

export default LeaveListPage;
