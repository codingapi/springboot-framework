import {loadFiles, upload} from "@/api/oss";

class OSSUtils{

    static uploadFile = async (filename:string,base64:string)=>{
        const response = await upload({
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
    }

    static loadFile = async (ids: string) => {
        const res = await loadFiles(ids);
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

export default OSSUtils;
