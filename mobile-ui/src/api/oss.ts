import {get, post} from "@/api/index";


export async function loadFiles(ids: string) {
    return get('/api/oss/load', {ids: ids});
}


export async function upload(body: any) {
    return post('/api/oss/upload', body);
}
