import React from "react";
import Header from "@/layout/Header";
import {list} from "@/api/leave";
import List from "@/components/list";


const LeaveListPage = () => {
    const handlerRefresh = async (pageSize: number) => {
        return list("", pageSize);
    }

    const handlerLoadMore = async (last: any, pageSize: number) => {
        return list(last, pageSize);
    }

    return (
        <>
            <Header>请假历史</Header>
            <List
                pageSize={2}
                item={(item, index) => {
                    return (
                        <div style={{
                            height: '300px',
                            fontSize: 16
                        }}>
                            {item.id} {item.username}
                        </div>
                    )
                }}
                onRefresh={handlerRefresh}
                onLoadMore={handlerLoadMore}
            />

        </>
    )
}

export default LeaveListPage;
