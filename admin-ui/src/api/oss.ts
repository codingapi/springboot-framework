import {httpClient} from "@/api/index";


export async function loadFiles(ids: string) {
    return httpClient.get('/api/oss/load', {ids: ids});
}


export async function upload(body: any) {
    return httpClient.post('/api/oss/upload', body);
}
