import {httpClient} from "@/api/index";

export async function upload(body: any) {
    return httpClient.post('/api/jar/upload', body);
}

export async function restart() {
    return httpClient.post('/api/jar/restart',{});
}
