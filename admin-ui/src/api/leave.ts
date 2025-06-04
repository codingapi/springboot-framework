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
    return httpClient.page('/api/query/leave/list', params, sort, filter, match);
}



export async function startLeave(body: any) {
    return httpClient.post('/api/cmd/leave/startLeave', body);
}

