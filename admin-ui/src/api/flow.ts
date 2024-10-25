import {get, page, post} from "@/api/index";


// 流程设计

export async function list(
    params: any,
    sort: any,
    filter: any,
    match: {
        key: string,
        type: string
    }[]
) {
    return page('/api/query/flowWork/list', params, sort, filter, match);
}


export async function save(body: any) {
    return post('/api/cmd/flowWork/save', body);
}

export async function remove(id: any) {
    return post('/api/cmd/flowWork/delete', {id});
}

export async function changeState(id: any) {
    return post('/api/cmd/flowWork/changeState', {id});
}

export async function schema(body: any) {
    return post('/api/cmd/flowWork/schema', body);
}

// 流程控制

export async function detail(id:any) {
    return get('/api/query/flowRecord/detail', {id});
}

export async function saveFlow(body:any) {
    return post('/api/cmd/flowRecord/save', body);
}

export async function submitFlow(body:any) {
    return post('/api/cmd/flowRecord/submitFlow', body);
}

// 待办中心控制

export async function flowRecordList(params: any,
                                     sort: any,
                                     filter: any,
                                     match: {
                                         key: string,
                                         type: string
                                     }[]) {
    return page('/api/query/flowRecord/list', params, sort, filter, match);
}


export async function findTodoByOperatorId(params: any,
                                     sort: any,
                                     filter: any,
                                     match: {
                                         key: string,
                                         type: string
                                     }[]) {
    return page('/api/query/flowRecord/findTodoByOperatorId', params, sort, filter, match);
}

export async function findDoneByOperatorId(params: any,
                                           sort: any,
                                           filter: any,
                                           match: {
                                               key: string,
                                               type: string
                                           }[]) {
    return page('/api/query/flowRecord/findDoneByOperatorId', params, sort, filter, match);
}


export async function findInitiatedByOperatorId(params: any,
                                           sort: any,
                                           filter: any,
                                           match: {
                                               key: string,
                                               type: string
                                           }[]) {
    return page('/api/query/flowRecord/findInitiatedByOperatorId', params, sort, filter, match);
}


export async function findTimeoutTodoByOperatorId(params: any,
                                                sort: any,
                                                filter: any,
                                                match: {
                                                    key: string,
                                                    type: string
                                                }[]) {
    return page('/api/query/flowRecord/findTimeoutTodoByOperatorId', params, sort, filter, match);
}

export async function findPostponedTodoByOperatorId(params: any,
                                                  sort: any,
                                                  filter: any,
                                                  match: {
                                                      key: string,
                                                      type: string
                                                  }[]) {
    return page('/api/query/flowRecord/findPostponedTodoByOperatorId', params, sort, filter, match);
}
