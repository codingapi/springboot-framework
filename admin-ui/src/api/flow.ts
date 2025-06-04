import {httpClient} from "@/api/index";


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
    return httpClient.page('/api/query/flowWork/list', params, sort, filter, match);
}


export async function save(body: any) {
    return httpClient.post('/api/cmd/flowWork/save', body);
}

export async function copy(id: any) {
    return httpClient.post('/api/cmd/flowWork/copy', {id});
}

export async function remove(id: any) {
    return httpClient.post('/api/cmd/flowWork/delete', {id});
}

export async function changeState(id: any) {
    return httpClient.post('/api/cmd/flowWork/changeState', {id});
}

export async function schema(body: any) {
    return httpClient.post('/api/cmd/flowWork/schema', body);
}

// 流程控制

export async function startFlow(body:any) {
    return httpClient.post('/api/cmd/flowRecord/startFlow', body);
}

export async function getFlowStep(body:any) {
    return httpClient.post('/api/cmd/flowRecord/getFlowStep', body);
}

export async function removeFlow(body:any) {
    return httpClient.post('/api/cmd/flowRecord/remove', body);
}


export async function detail(id?:any,workCode?:any) {
    return httpClient.get('/api/query/flowRecord/detail', {id,workCode});
}

export async function saveFlow(body:any) {
    return httpClient.post('/api/cmd/flowRecord/save', body);
}

export async function submitFlow(body:any) {
    return httpClient.post('/api/cmd/flowRecord/submitFlow', body);
}

export async function trySubmitFlow(body:any) {
    return httpClient.post('/api/cmd/flowRecord/trySubmitFlow', body);
}

export async function custom(body:any) {
    return httpClient.post('/api/cmd/flowRecord/custom', body);
}

export async function recall(body:any) {
    return httpClient.post('/api/cmd/flowRecord/recall', body);
}

export async function postponed(body:any) {
    return httpClient.post('/api/cmd/flowRecord/postponed', body);
}

export async function transfer(body:any) {
    return httpClient.post('/api/cmd/flowRecord/transfer', body);
}

export async function urge(body:any) {
    return httpClient.post('/api/cmd/flowRecord/urge', body);
}


// 待办中心控制

export async function flowRecordList(params: any,
                                     sort: any,
                                     filter: any,
                                     match: {
                                         key: string,
                                         type: string
                                     }[]) {
    return httpClient.page('/api/query/flowRecord/list', params, sort, filter, match);
}


export async function findAllByOperatorId(params: any,
                                     sort: any,
                                     filter: any,
                                     match: {
                                         key: string,
                                         type: string
                                     }[]) {
    return httpClient.page('/api/query/flowRecord/findAllByOperatorId', params, sort, filter, match);
}


export async function findTodoByOperatorId(params: any,
                                     sort: any,
                                     filter: any,
                                     match: {
                                         key: string,
                                         type: string
                                     }[]) {
    return httpClient.page('/api/query/flowRecord/findTodoByOperatorId', params, sort, filter, match);
}

export async function findDoneByOperatorId(params: any,
                                           sort: any,
                                           filter: any,
                                           match: {
                                               key: string,
                                               type: string
                                           }[]) {
    return httpClient.page('/api/query/flowRecord/findDoneByOperatorId', params, sort, filter, match);
}


export async function findInitiatedByOperatorId(params: any,
                                           sort: any,
                                           filter: any,
                                           match: {
                                               key: string,
                                               type: string
                                           }[]) {
    return httpClient.page('/api/query/flowRecord/findInitiatedByOperatorId', params, sort, filter, match);
}


export async function findTimeoutTodoByOperatorId(params: any,
                                                sort: any,
                                                filter: any,
                                                match: {
                                                    key: string,
                                                    type: string
                                                }[]) {
    return httpClient.page('/api/query/flowRecord/findTimeoutTodoByOperatorId', params, sort, filter, match);
}

export async function findPostponedTodoByOperatorId(params: any,
                                                  sort: any,
                                                  filter: any,
                                                  match: {
                                                      key: string,
                                                      type: string
                                                  }[]) {
    return httpClient.page('/api/query/flowRecord/findPostponedTodoByOperatorId', params, sort, filter, match);
}
