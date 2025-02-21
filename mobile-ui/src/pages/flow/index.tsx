import React, {useEffect} from "react";
import Header from "@/layout/Header";
import List, {ListAction} from "@/components/list";
import {RightOutline} from "antd-mobile-icons";
import {
    findDoneByOperatorId,
    findInitiatedByOperatorId,
    findPostponedTodoByOperatorId,
    findTimeoutTodoByOperatorId,
    findTodoByOperatorId,
    findAllByOperatorId,
} from "@/api/flow";
import {Tabs} from "antd-mobile";
import moment from "moment";
import "./index.scss";
import {useNavigate} from "react-router";


interface TodoItemProps{
    item:any;
}

const TodoItem:React.FC<TodoItemProps> = (props) => {

    const item = props.item;

    const navigate = useNavigate();

    return (
        <div className={"flow-todo-item"}>
            <div
                className={"flow-todo-item-content"}
            >
                <div className={"flow-todo-item-title"}>{item.title}</div>
                <div className={"flow-todo-item-attr"}>
                    <div className={"flow-todo-item-attr-title"}>审批人:</div>
                    <div className={"flow-todo-item-attr-content"}>{item.currentOperatorName}</div>
                </div>
                <div className={"flow-todo-item-attr"}>
                    <div className={"flow-todo-item-attr-title"}>发起人:</div>
                    <div className={"flow-todo-item-attr-content"}>{item.createOperatorName}</div>
                </div>
                <div className={"flow-todo-item-attr"}>
                    <div className={"flow-todo-item-attr-title"}>创建时间:</div>
                    <div className={"flow-todo-item-attr-content"}>{moment(item.createTime).format('YYYY-MM-DD HH:mm:ss')}</div>
                </div>
            </div>
            <div
                className={"flow-todo-item-arrow"}>
                <RightOutline
                    fontSize={20}
                    onClick={()=>{
                        navigate('/flow/detail',{state:item});
                    }}
                />
            </div>
        </div>
    )
}

const FlowListPage = () => {

    const [key, setKey] = React.useState('todo');

    const loadList = async (last: any, pageSize: number) => {
        if (key === 'todo') {
            return findTodoByOperatorId(last, pageSize);
        }
        if (key === 'done') {
            return findDoneByOperatorId(last, pageSize);
        }
        if (key === 'initiated') {
            return findInitiatedByOperatorId(last, pageSize);
        }
        if (key === 'timeoutTodo') {
            return findTimeoutTodoByOperatorId(last, pageSize);
        }
        if (key === 'postponedTodo') {
            return findPostponedTodoByOperatorId(last, pageSize);
        }
        if (key === 'all') {
            return findAllByOperatorId(last, pageSize);
        }
    }

    const handlerRefresh = async (pageSize: number) => {
        return loadList("", pageSize);
    }

    const handlerLoadMore = async (last: any, pageSize: number) => {
        return loadList(last, pageSize);
    }

    const listAction = React.useRef<ListAction>(null);

    useEffect(() => {
        listAction.current?.reload();
    }, [key]);

    return (
        <>
            <Header>待办中心</Header>
            <div className={"flow-todo-content"}>
                <Tabs
                    className={"flow-todo-tabs"}
                    activeKey={key}
                    onChange={setKey}
                >
                    <Tabs.Tab title={"待办"} key={"todo"}/>
                    <Tabs.Tab title={"已办"} key={"done"}/>
                    <Tabs.Tab title={"我的发起"} key={"initiated"}/>
                    <Tabs.Tab title={"延期待办"} key={"timeoutTodo"}/>
                    <Tabs.Tab title={"超时待办"} key={"postponedTodo"}/>
                    <Tabs.Tab title={"全部流程"} key={"all"}/>
                </Tabs>

                <List
                    listAction={listAction}
                    className={"flow-todo-list"}
                    item={(item, index) => {
                        return <TodoItem item={item}/>
                    }}
                    onRefresh={handlerRefresh}
                    onLoadMore={handlerLoadMore}
                />
            </div>
        </>
    )
}

export default FlowListPage;
