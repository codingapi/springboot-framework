import {page, post} from "@/api/index";

export async function list(
    params: any,
    sort: any,
    filter: any,
    match: {
        key: string,
        type: string
    }[]
) {
    return page('/api/node/list', params, sort, filter, match);
}


export async function save(body: any) {
    return post('/api/node/save', body);
}


export async function del(body: {
    id: string,
}) {
    return post('/api/node/delete', body);
}
