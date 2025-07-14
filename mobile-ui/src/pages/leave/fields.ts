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
            validateFunction: async (content) => {
                if (content.value <= 0) {
                    return ["请假天数不能小于0"];
                }
                return []
            }
        }
    },
    {
        type: "textarea",
        props: {
            label: "请假理由",
            name: "desc",
            required: true,
            validateFunction: async (content) => {
                if (content.value && content.value.length > 0) {
                    return []
                }
                return ["请假理由不能为空"];
            }
        }
    }
] as FormField[]
