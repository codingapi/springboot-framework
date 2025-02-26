import {get, post} from "@/api/index";

export async function list(
    lastId?: string,
    pageSize=10,
) {
    return get('/api/app/query/leave/list', {lastId,pageSize});
}


export async function startLeave(body: any) {
    return post('/api/cmd/leave/startLeave', body);
}

