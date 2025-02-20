import React, {useImperativeHandle} from "react";
import todo from "@/assets/flow/todo.png";
import un_submit from "@/assets/flow/un_submit.png";
import done from "@/assets/flow/done.png";
import {Button} from "antd-mobile";
import List, {ListAction, ListResponse} from "@/components/list";
import {DeleteOutline, EditSOutline, EyeOutline} from "antd-mobile-icons";
import "./index.scss";

export type InfoState = 'un_submit' | 'todo' | 'done' | 'reject';

const stateConvert = (state: InfoState) => {
    if (state === 'todo') {
        return todo;
    }
    if (state === 'un_submit') {
        return un_submit;
    }
    if (state === 'done') {
        return done;
    }
    return un_submit;
}

export type InfoItem = {
    id: number;
    state: InfoState;
    attrs: {
        [key: string]: any;
    },
    [key: string]: any;
}

interface InfoListProps {

    listAction?: React.Ref<ListAction>;
    // 详情事件
    onDetailClick?: (item: InfoItem) => void;
    // 编辑事件
    onEditClick?: (item: InfoItem) => void;
    // 删除事件
    onDeleteClick?: (item: InfoItem) => void;

    // 每页数量，默认为10
    pageSize?: number;
    // 刷新数据
    onRefresh?: (pageSize: number) => Promise<ListResponse>;
    // 加载更多
    onLoadMore?: (pageSize: number, last: any) => Promise<ListResponse>;
}

const InfoList: React.FC<InfoListProps> = (props) => {

    const listAction = React.useRef<ListAction>(null);

    useImperativeHandle(props.listAction, () => {
        return {
            reload: () => {
                if (listAction.current) {
                    listAction.current.reload();
                }
            }
        }
    }, [props.listAction])

    return (
        <div className={"infoList-list"}>
            <List
                listAction={listAction}
                pageSize={props.pageSize}
                item={(item, index) => {
                    const attrs = item.attrs;
                    const attrKeys = Object.keys(attrs);
                    return (
                        <div className="infoList-item">
                            <div className={"infoList-left"}>
                                {
                                    attrKeys.map((info) => {
                                        return (
                                            <div className={"infoList-info-item"}>
                                                <div className={"infoList-info-item-title"}>{info}</div>
                                                <div className={"infoList-info-item-value"}>{attrs[info]}</div>
                                            </div>
                                        )
                                    })
                                }
                            </div>
                            <div className={"infoList-right"}>
                                <div className={"infoList-state"}>
                                    <img src={stateConvert(item.state)} className={"infoList-state-img"}/>
                                </div>
                                <div className={"infoList-operate"}>
                                    <Button
                                        onClick={() => {
                                            props.onDeleteClick && props.onDeleteClick(item);
                                        }}
                                        className={"infoList-operate-button"}
                                        shape={'rounded'}
                                        style={{
                                            backgroundColor: 'red'
                                        }}
                                    >
                                        <DeleteOutline color={'white'}/>
                                    </Button>

                                    <Button
                                        onClick={() => {
                                            props.onEditClick && props.onEditClick(item);
                                        }}
                                        className={"infoList-operate-button"}
                                        shape={'rounded'}
                                        style={{
                                            backgroundColor: 'blue'
                                        }}
                                    >
                                        <EditSOutline color={'white'}/>
                                    </Button>

                                    <Button
                                        onClick={() => {
                                            props.onDetailClick && props.onDetailClick(item);
                                        }}
                                        className={"infoList-operate-button"}
                                        shape={'rounded'}
                                        style={{
                                            backgroundColor: 'blue'
                                        }}
                                    >
                                        <EyeOutline color={'white'}/>
                                    </Button>
                                </div>
                            </div>
                        </div>
                    )
                }}
                onRefresh={props.onRefresh}
                onLoadMore={props.onLoadMore}
            />
        </div>
    )
}

export default InfoList;
