import {httpClient} from "@/api/index";

export async function list(
    lastId?: string,
    pageSize=10,
) {
    return httpClient.get('/api/app/query/leave/list', {lastId,pageSize});
}


export async function startLeave(body: any) {
    return httpClient.post('/api/cmd/leave/startLeave', body);
}

