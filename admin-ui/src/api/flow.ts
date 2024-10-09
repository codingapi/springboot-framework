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
    return page('/api/flow/list', params, sort, filter, match);
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

export async function create(body: any) {
    return post('/api/flow/create', body);
}

export async function save(body: any) {
    return post('/api/flow/save', body);
}

export async function schema(body: any) {
    return post('/api/flow/schema', body);
}

export async function submit(body: any) {
    return post('/api/approval/submit', body);
}

export async function recall(recordId: string) {
    return post('/api/approval/recall', {recordId});
}
