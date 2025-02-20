import React from "react";
import "./index.scss";
import todo from "@/assets/flow/todo.png";
import {Button} from "antd-mobile";
import {DeleteOutline, EditSOutline, EyeOutline} from "antd-mobile-icons";

export type InfoState = 'un_submit' | 'todo' | 'pass' | 'reject';

export type InfoItem = {
    id: number;
    state: InfoState;
    [key: string]: any;
}

interface InfoListProps {
    list: InfoItem[];
    onDetailClick:(item:InfoItem)=>void;
}

const InfoList: React.FC<InfoListProps> = (props) => {

    const list = props.list;

    return (
        <div className={"infoList-list"}>
            {list.map((item, index) => {
                const infoList = Object.keys(item).filter(key => key !== 'id' && key !== 'state');

                return (
                    <div className="infoList-item">
                        <div className={"infoList-left"}>
                            {
                                infoList.map((info) => {
                                    return (
                                        <div className={"infoList-info-item"}>
                                            <div className={"infoList-info-item-title"}>{info}</div>
                                            <div className={"infoList-info-item-value"}>{item[info]}</div>
                                        </div>
                                    )
                                })
                            }
                        </div>
                        <div className={"infoList-right"}>
                            <div className={"infoList-state"}>
                                <img src={todo} className={"infoList-state-img"}/>
                            </div>
                            <div className={"infoList-operate"}>
                                <Button
                                    className={"infoList-operate-button"}
                                    shape={'rounded'}
                                    style={{
                                        backgroundColor:'red'
                                    }}
                                >
                                    <DeleteOutline color={'white'}/>
                                </Button>

                                <Button
                                    className={"infoList-operate-button"}
                                    shape={'rounded'}
                                    style={{
                                        backgroundColor:'blue'
                                    }}
                                >
                                    <EditSOutline color={'white'}/>
                                </Button>

                                <Button
                                    onClick={()=>{
                                        props.onDetailClick(item);
                                    }}
                                    className={"infoList-operate-button"}
                                    shape={'rounded'}
                                    style={{
                                        backgroundColor:'blue'
                                    }}
                                >
                                    <EyeOutline color={'white'}/>
                                </Button>
                            </div>
                        </div>
                    </div>
                )
            })}
        </div>
    )
}

export default InfoList;
