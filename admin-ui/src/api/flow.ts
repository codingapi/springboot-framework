import {get, page, post} from "@/api/index";

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


//



export async function create(body: any) {
    return post('/api/cmd/flowWork/save', body);
}


export async function todo() {
    return get('/api/approval/todo');
}

export async function done() {
    return get('/api/approval/done');
}

export async function creator() {
    return get('/api/approval/creator');
}




export async function submit(body: any) {
    return post('/api/approval/submit', body);
}

export async function recall(recordId: string) {
    return post('/api/approval/recall', {recordId});
}
