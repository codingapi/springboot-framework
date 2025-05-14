import {Toast} from "antd-mobile";
import {HttpClient} from "@codingapi/ui-framework";

export const httpClient = new HttpClient(10000,{
    success: (msg: string) => {
        Toast.show({
            content: msg,
        })
    },
    error: (msg: string) => {
        Toast.show({
            icon: 'fail',
            content: msg,
        })
    }
})
