import React from "react";
import Form from "@/components/form";
import {FormField} from "@/components/form/types";

const LeaveForm = ()=>{
    return (
        <Form
            initialValues={{
                days: 1
            }}
            loadFields={async ()=>{
                return [
                    {
                        type:"input",
                        props:{
                            label:"请假天数",
                            name:"days",
                            required:true,
                            rules:[
                                {
                                    required:true,
                                    message:'请输入请假天数'
                                }
                            ]
                        }
                    },
                    {
                        type:"textarea",
                        props:{
                            label:"请假理由",
                            name:"desc",
                            required:true,
                            rules:[
                                {
                                    required:true,
                                    message:'请输入请假理由'
                                }
                            ]
                        }
                    }
                ] as FormField[]
            }}
        />
    );
}

export default LeaveForm;
