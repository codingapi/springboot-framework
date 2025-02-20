import React from "react";
import Header from "@/layout/Header";
import {list} from "@/api/leave";
import List from "@/components/list";
import {Button} from "antd-mobile";
import {RightOutline} from "antd-mobile-icons";
import {useNavigate} from "react-router";

const leaveItem = (item: any) => {
    return (
        <div style={{
            backgroundColor: 'white',
            marginTop: '5px',
            position: 'relative'
        }}>
            <div style={{
                fontSize: '16px',
                padding: 5
            }}>
                <div
                    style={{
                        marginTop: 5
                    }}
                >请假人:{item.username}</div>
                <div
                    style={{
                        marginTop: 5
                    }}
                >请假天数:{item.days}</div>
                <div
                    style={{
                        marginTop: 5
                    }}
                >请假理由:{item.desc}</div>
            </div>

            <div style={{
                position: 'absolute',
                right: 10,
                top: '45%'
            }}>
                <RightOutline
                    fontSize={20}
                />
            </div>
        </div>
    )
}

const LeaveListPage = () => {
    const handlerRefresh = async (pageSize: number) => {
        return list("", pageSize);
    }

    const handlerLoadMore = async (last: any, pageSize: number) => {
        return list(last, pageSize);
    }

    const navigate = useNavigate();

    return (
        <>
            <Header>请假历史</Header>
            <List
                style={{
                    height: 'calc(100vh - 120px)',
                    overflow: 'auto'
                }}
                item={(item, index) => {
                    return leaveItem(item);
                }}
                onRefresh={handlerRefresh}
                onLoadMore={handlerLoadMore}
            />
            <div
                style={{
                    height: 60,
                }}
            >
                <Button
                    onClick={() => {
                        navigate("/leave/create");
                    }}
                    color={'primary'}
                    block={true}
                >请假</Button>
            </div>
        </>
    )
}

export default LeaveListPage;
