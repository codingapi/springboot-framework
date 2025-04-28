import React from "react";
import {Form} from "@codingapi/form-mobile";
import {Button, Modal} from "antd-mobile";
import {FormField} from "@codingapi/ui-framework";

const Test = () => {

    const [visible, setVisible] = React.useState(false);
    const fields = [
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
    ] as FormField[];

    const formInstance = Form.useForm();


    return (
        <div>
            <div style={{
                display:'flex',
                justifyItems:'center',
                justifyContent:'center',
            }}>
                <Button
                    onClick={()=>{
                        formInstance.setFieldValue('desc','123');
                        setVisible(true);
                    }}
                >show form</Button>
            </div>

            <Modal
                visible={visible}
                closeOnMaskClick={true}
                title={"Form表单测试"}
                onClose={() => {
                    setVisible(false)
                }}
                closeOnAction={true}
                content={(
                    <Form
                        form={formInstance}
                        loadFields={async ()=>{
                            return fields
                        }}
                    />
                )}
            />
        </div>
    )
}

export default Test
