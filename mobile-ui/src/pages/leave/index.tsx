import React from "react";
import Header from "@/layout/Header";
import {list} from "@/api/leave";
import {PullToRefreshList} from "@codingapi/form-mobile";
import {Button} from "antd-mobile";
import {RightOutline} from "antd-mobile-icons";
import {useNavigate} from "react-router";
import Footer from "@/layout/Footer";


interface LeaveItemProps{
    item:any;
}

const LeaveItem:React.FC<LeaveItemProps> = (props) => {
    const item = props.item;
    const navigate = useNavigate();

    return (
        <div style={{
            backgroundColor: 'white',
            marginTop: '1px',
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
                    onClick={()=>{
                        navigate(`/leave/detail`,{state:item});
                    }}
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
            <PullToRefreshList
                style={{
                    height: 'calc(100vh - 100px)',
                    overflow: 'auto'
                }}
                item={(item, index) => {
                    return (
                        <LeaveItem item={item}/>
                    );
                }}
                onRefresh={handlerRefresh}
                onLoadMore={handlerLoadMore}
            />
            <Footer>
                <Button
                    style={{
                        margin:"15%"
                    }}
                    onClick={() => {
                        navigate("/leave/create");
                    }}
                    color={'primary'}
                    block={true}
                >请假</Button>
            </Footer>
        </>
    )
}

export default LeaveListPage;
