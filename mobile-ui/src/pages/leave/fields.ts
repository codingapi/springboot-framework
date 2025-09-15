import {FormField} from "@codingapi/ui-framework";

export const fields = [
    {
        type: "input",
        props: {
            name: "clazzName",
            hidden: true
        }
    },
    {
        type: "input",
        props: {
            name: "username",
            hidden: true
        }
    },
    {
        type: "input",
        props: {
            label: "请假天数",
            name: "days",
            required: true,
            rules: [
                {
                    required: true,
                    message: "请假天数不能为空"
                }
            ],
        }
    },
    {
        type: "textarea",
        props: {
            label: "请假理由",
            name: "desc",
            required: true,
            rules:[
                {
                    required: true,
                    message: "请假理由不能为空"
                }
            ]
        }
    }
] as FormField[]
