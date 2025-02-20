import {fromByteArray} from "base64-js";
import moment from "moment";

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


export const currentLocalDate = () => {
    const format = 'yyyy年MM月DD日';
    const date = new Date();
    return moment(date).format(format);
}
