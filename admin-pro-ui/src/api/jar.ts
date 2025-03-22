import {post} from "@/api/index";

export async function upload(body: any) {
    return post('/api/jar/upload', body);
}

export async function restart() {
    return post('/api/jar/restart',{});
}
