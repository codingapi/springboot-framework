import React from "react";
import "./index.scss";
import {ErrorBlock, InfiniteScroll, PullToRefresh} from "antd-mobile";
import {PullStatus} from "antd-mobile/es/components/pull-to-refresh";

export interface ListResponse {
    data: {
        total: number;
        list: any[]
    }
}

export interface ListAction {
    reload:()=>void;
}

export interface ListProps {
    listAction?:React.Ref<ListAction>;
    // 每页数量，默认为10
    pageSize?: number;
    // 刷新数据
    onRefresh?: (pageSize: number) => Promise<ListResponse>;
    // 加载更多
    onLoadMore?: (pageSize: number, last: any) => Promise<ListResponse>;
    // 渲染列表项
    item: (item: any, index: number) => React.ReactNode;
    // 拉去数据提示
    pullStates?: {
        // 默认值，用力拉
        pulling: string;
        // 默认值，松开吧
        canRelease: string;
        // 默认值，玩命加载中...
        refreshing: string;
        // 默认值，好啦
        complete: string
    };
    // 空数据提示
    blockStates?: {
        // 默认值，暂无信息
        title: string;
        // 默认值，没有任何信息
        description: string;
    }
}

const List: React.FC<ListProps> = (props) => {

    const pageSize = props.pageSize || 10;
    const [orderList, setOrderList] = React.useState<any>([]);
    const [last, setLast] = React.useState<any>("");
    const [noData, setNoData] = React.useState(false);
    const [hasMore, setHasMore] = React.useState(true);

    const [loading, setLoading] = React.useState(false);

    const statusRecord: Record<PullStatus, string> = {
        pulling: props.pullStates?.pulling || '用力拉',
        canRelease: props.pullStates?.canRelease || '松开吧',
        refreshing: props.pullStates?.refreshing || '玩命加载中...',
        complete: props.pullStates?.complete || '好啦',
    }

    React.useImperativeHandle(props.listAction, () => ({
        reload: () => {
            refresh();
        }
    }), [props.listAction])

    const loadMore = async () => {
        if (loading) {
            return;
        }
        if (props.onLoadMore) {
            setLoading(true);
            props.onLoadMore(last, pageSize)
                .then(res => {
                    const {data} = res;
                    if (data.total > 0) {
                        const list = data.list;
                        const last = list[list.length - 1].id;
                        setLast(last);
                        const currentList = orderList;

                        for (let i = 0; i < list.length; i++) {
                            const item = list[i];
                            if (currentList.find((value: any) => value.id === item.id)) {
                                continue;
                            }
                            currentList.push(item);
                        }

                        setOrderList(currentList);
                        setHasMore(data.total > list.length);
                    } else {
                        setHasMore(false);
                        if(!last) {
                            setNoData(true);
                        }
                    }
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    }

    const refresh = () => {
        if (loading) {
            return;
        }
        if (props.onRefresh) {
            setLoading(true);
            props.onRefresh(pageSize)
                .then(res => {
                    const {data} = res;
                    if (data.total > 0) {
                        const list = data.list;
                        const last = list[list.length - 1].id;
                        setLast(last);
                        setOrderList(list);
                        setHasMore(data.total > list.length);
                    } else {
                        setHasMore(false);
                        setNoData(true)
                    }
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    }

    return (
        <div
            className={"mobile-list"}
        >
            <PullToRefresh
                onRefresh={async () => {
                    refresh();
                }}
                renderText={status => {
                    return <div>{statusRecord[status]}</div>
                }}
            >
                {orderList && orderList.map((item: any, index: number) => {
                    return props.item(item, index);
                })}

                {!noData && (
                    <InfiniteScroll loadMore={loadMore} hasMore={hasMore}/>
                )}
                {noData && (
                    <ErrorBlock
                        status='empty'
                        style={{
                            '--image-height': '120px',
                        }}
                        description={props.blockStates?.description || '没有任何信息'}
                        title={props.blockStates?.title || "暂无信息"}/>
                )}
            </PullToRefresh>

        </div>
    )
}

export default List;
