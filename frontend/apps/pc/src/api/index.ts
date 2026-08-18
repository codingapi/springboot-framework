import {message} from "antd";
import {HttpClient} from "@codingapi/ui-framework";

export const httpClient = new HttpClient(10000, {
    success:(msg:string)=>{
        message.success(msg).then();
    },
    error:(msg:string)=>{
        message.error(msg).then();
    }
})
