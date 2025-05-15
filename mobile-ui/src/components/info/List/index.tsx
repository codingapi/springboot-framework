import React, {useEffect, useImperativeHandle} from "react";
import todo from "@/assets/flow/todo.png";
import un_submit from "@/assets/flow/un_submit.png";
import done from "@/assets/flow/done.png";
import {ListAction, PullToRefreshList} from "@codingapi/form-mobile";
import {Response} from "@codingapi/ui-framework";
import ListItem from "@/components/info/List/Item";
import "./index.scss";

export type InfoState = 'un_submit' | 'todo' | 'done' | 'reject';

export const stateConvert = (state: InfoState) => {
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

export type TodoListItem = {
    id: number;
    state: InfoState;
    attrs: {
        [key: string]: any;
    },
    [key: string]: any;
}

interface PullToRefreshTodoListProps {

    listAction?: React.Ref<ListAction>;
    // 详情事件
    onDetailClick?: (item: TodoListItem) => void;
    // 编辑事件
    onEditClick?: (item: TodoListItem) => void;
    // 删除事件
    onDeleteClick?: (item: TodoListItem) => void;

    // 每页数量，默认为10
    pageSize?: number;
    // 刷新数据
    onRefresh?: (pageSize: number) => Promise<Response>;
    // 加载更多
    onLoadMore?: (pageSize: number, last: any) => Promise<Response>;


    showDetail?: (item: TodoListItem) => boolean;
    showEdit?: (item: TodoListItem) => boolean;
    showDelete?: (item: TodoListItem) => boolean;

}

export const PullToRefreshTodoList: React.FC<PullToRefreshTodoListProps> = (props) => {

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
            <PullToRefreshList
                listAction={listAction}
                pageSize={props.pageSize}
                item={(item, index) => {
                    return (
                        <ListItem
                            key={index}
                            data={item}
                            onDetailClick={props.onDetailClick}
                            onEditClick={props.onEditClick}
                            onDeleteClick={props.onDeleteClick}
                            showDetail={props.showDetail}
                            showEdit={props.showEdit}
                            showDelete={props.showDelete}
                        />
                    )
                }}
                onRefresh={props.onRefresh}
                onLoadMore={props.onLoadMore}
            />
        </div>
    )
}


interface TodoListProps {

    listAction?: React.Ref<ListAction>;
    // 详情事件
    onDetailClick?: (item: TodoListItem) => void;
    // 编辑事件
    onEditClick?: (item: TodoListItem) => void;
    // 删除事件
    onDeleteClick?: (item: TodoListItem) => void;
    // 加载数据
    loadData: () => Promise<Response>;


    showDetail?: (item: TodoListItem) => boolean;
    showEdit?: (item: TodoListItem) => boolean;
    showDelete?: (item: TodoListItem) => boolean;

}


export const TodoList: React.FC<TodoListProps> = (props) => {

    const [list, setList] = React.useState<TodoListItem[]>([]);

    const reload = () => {
        props.loadData().then((res) => {
            if (res.success) {
                setList(res.data.list);
            }
        });
    }

    useEffect(() => {
        reload();
    }, []);

    useImperativeHandle(props.listAction, () => {
        return {
            reload: () => {
                reload();
            }
        }
    }, [props.listAction])

    return (
        <div className={"infoList-list"}>
            {list.map(item => {
                return (
                    <ListItem
                        data={item}
                        onDetailClick={props.onDetailClick}
                        onEditClick={props.onEditClick}
                        onDeleteClick={props.onDeleteClick}
                        showDetail={props.showDetail}
                        showEdit={props.showEdit}
                        showDelete={props.showDelete}
                    />
                )
            })}
        </div>
    )
}
