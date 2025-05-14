import {httpClient} from "@/api/index";

export async function list(
    params: any,
    sort: any,
    filter: any,
    match: {
        key: string,
        type: string
    }[]
) {
    return httpClient.page('/api/query/user/list', params, sort, filter, match);
}

export async function users() {
    return httpClient.get('/api/query/user/list', {current:1,pageSize:999999});
}


export async function save(body: any) {
    return httpClient.post('/api/cmd/user/save', body);
}

export async function entrust(body: any) {
    return httpClient.post('/api/cmd/user/entrust', body);
}

export async function removeEntrust(id: any) {
    return httpClient.post('/api/cmd/user/removeEntrust', {id});
}


export async function changeManager(id: any) {
    return httpClient.post('/api/cmd/user/changeManager', {id});
}


export async function remove(id: any) {
    return httpClient.post('/api/cmd/user/remove', {id});
}
