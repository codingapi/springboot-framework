import {fromByteArray} from "base64-js";

export const sleep = async (time: number) => {
    return new Promise((resolve:any) => {
        setTimeout(() => {
            resolve();
        }, time);
    })
}


export const textToBase64 = (text: any) => {
    const encoder = new TextEncoder();
    const urlArray = encoder.encode(JSON.stringify(text));
    return fromByteArray(urlArray);
}
