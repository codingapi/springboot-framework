import {RcFile} from "antd/es/upload";

export const rcFileToBase64 = (file: RcFile): Promise<string> => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result as string);
        reader.onerror = error => reject(error);
    });
}


export const base64ToBlob = (base64: string, type: string) => {
    const binStr = atob(base64.split(',')[1]);
    const len = binStr.length;
    const arr = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
        arr[i] = binStr.charCodeAt(i);
    }
    return new Blob([arr], {type: type});
}

export const base64ToString = (base64: string) => {
    if (base64.indexOf('base64') !== -1) {
        base64 = base64.split(',')[1];
    }
    return atob(base64);
}
