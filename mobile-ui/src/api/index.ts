import axios from "axios";
import {sleep, textToBase64} from "@/utils";
import {Toast} from "antd-mobile";

const api = axios.create({
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.request.use((config: any) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers = {
            Authorization: `${token}`,
        } as any;
    }
    return config;
}, (error: any) => {
    return Promise.reject(error);
});

api.interceptors.response.use(async (response: any) => {
        const headers = response.headers;
        const token = headers['authorization'];

        const state = response.status;
        if (state === 200) {
            if (token) {
                console.log('reset token', token);
                localStorage.setItem("token", token)
            }
            if(response.data) {
                const success = response.data.success;
                if (!success) {
                    const errMessage = response.data.errMessage;
                    const errCode = response.data.errCode;
                    if ("token.expire" === errCode || "token.error" === errCode) {
                        Toast.show({
                            content: '登录已过期，请退出再重新打开',
                        })
                        await sleep(1500);
                        localStorage.clear();
                        window.location.href = '/#login';
                    } else {
                        if ("login.error" === errCode) {
                            return response;
                        }
                        Toast.show({
                            icon: 'fail',
                            content:errMessage,
                        })
                    }
                }
            }else {
                Toast.show({
                    icon: 'fail',
                    content:'抱歉，该账户无权限访问',
                })
            }
        }
        return response;
    },
    (error: any) => {
        const response = error.response;
        const state = response.data.status;

        if(state === 403){
            Toast.show({
                icon: 'fail',
                content:'抱歉，该账户无权限访问',
            })
            return {
                data: {
                    success: false,
                }
            }
        }
        return Promise.reject(error);
    }
)


export const get = async (url: string, params?: any) => {
    try {
        const response = await api.get(url, {
            params
        });
        return response.data;
    }catch (e){
        return {
            success: false,
        }
    }
}

export const post = async (url: string, data: any) => {
    try {
        const response = await api.post(url, data);
        return response.data;
    }catch (e){
        return {
            success: false,
        }
    }
}

export const page = async (url: string, params: any, sort: any, filter: any, match: {
    key: string,
    type: string
}[]) => {
    const response = await get(url, {
        ...params,
        sort: textToBase64(sort),
        filter: textToBase64(filter),
        params: textToBase64(match),
    });

    if(response.success){
        const list = response.data.total > 0 ? response.data.list : [];
        return {
            data: list,
            success: response.success,
            total: response.data.total
        };
    }else{
        return {
            data: [],
            success: response.success,
            total: 0
        }
    }
}


export const getDownload = async (url: string, filename?: string) => {
    try {
        const token = localStorage.getItem("token");
        const response = await axios.get(url, {
            responseType: 'blob',
            headers: {
                'Authorization': token,
            }
        });
        const bytes = await response.data;
        const blob = new Blob([bytes]);
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = filename || 'result.csv';
        a.click();
    } catch (e) {
        console.log(e);
    }
}

export const postDownload = async (url: string, data: any, filename?: string) => {
    try {
        const token = localStorage.getItem("token");
        const response = await axios.post(url, data, {
            responseType: 'blob',
            headers: {
                'Authorization': token,
            }
        });
        const bytes = await response.data;
        const blob = new Blob([bytes]);
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = filename || 'result.csv';
        a.click();
    } catch (e) {
        console.log(e);
    }
}
