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
    return page('/api/query/user/list', params, sort, filter, match);
}


export async function save(body: any) {
    return post('/api/cmd/user/save', body);
}

export async function entrust(body: any) {
    return post('/api/cmd/user/entrust', body);
}

export async function removeEntrust(id: any) {
    return post('/api/cmd/user/removeEntrust', {id});
}


export async function changeManager(id: any) {
    return post('/api/cmd/user/changeManager', {id});
}


export async function remove(id: any) {
    return post('/api/cmd/user/remove', {id});
}
