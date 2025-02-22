import React from "react";
import {TodoListItem, stateConvert} from "@/components/info/List/index";
import {Button, Dialog} from "antd-mobile";
import {DeleteOutline, EditSOutline, EyeOutline} from "antd-mobile-icons";


interface ListItemProps {
    data:TodoListItem;

    // 详情事件
    onDetailClick?: (item: TodoListItem) => void;
    // 编辑事件
    onEditClick?: (item: TodoListItem) => void;
    // 删除事件
    onDeleteClick?: (item: TodoListItem) => void;

}

const ListItem: React.FC<ListItemProps> = (props) => {
    const item = props.data;
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
                        onClick={async () => {
                            await Dialog.confirm({
                                content: '确认要删除吗？',
                                onConfirm: async () => {
                                    props.onDeleteClick && props.onDeleteClick(item);
                                },
                            })
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
}

export default ListItem;
