import type {HttpClient, Response} from "@codingapi/ui-framework";

export interface OssFile {
    url: string;
    id: any;
    name: string;
}

export interface OssApi {
    loadFiles: (ids: string) => Promise<Response>;
    upload: (body: any) => Promise<Response>;
}

export const createOssApi = (httpClient: HttpClient): OssApi => {

    async function loadFiles(ids: string) {
        return httpClient.get('/api/oss/load', {ids: ids});
    }

    async function upload(body: any) {
        return httpClient.post('/api/oss/upload', body);
    }

    return {loadFiles, upload};
}

export interface OSSUtilsClass {
    uploadFile: (filename: string, base64: string) => Promise<OssFile | null>;
    loadFile: (ids: string) => Promise<OssFile[] | undefined>;
}

export const createOSSUtils = (ossApi: OssApi): OSSUtilsClass => {

    class OSSUtils {

        static uploadFile = async (filename: string, base64: string) => {
            const response = await ossApi.upload({
                name: filename,
                data: base64
            })
            if (response.success) {
                const data = response.data;
                const url = `/open/oss/${data.key}`;
                return {
                    url: url,
                    id: data.id,
                    name: data.name
                }
            }
            return null;
        }

        static loadFile = async (ids: string) => {
            const res = await ossApi.loadFiles(ids);
            if (res.success) {
                const list = res.data.list;
                return list.map((item: any) => {
                    const url = `/open/oss/${item.key}`;
                    return {
                        url: url,
                        id: item.id,
                        name: item.name
                    }
                })
            }
        }
    }

    return OSSUtils;
}
