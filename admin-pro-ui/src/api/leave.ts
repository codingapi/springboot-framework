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
    return page('/api/query/leave/list', params, sort, filter, match);
}



export async function startLeave(body: any) {
    return post('/api/cmd/leave/startLeave', body);
}

