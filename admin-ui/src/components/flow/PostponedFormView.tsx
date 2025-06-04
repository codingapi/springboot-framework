import React from "react";
import {ModalForm, ProFormDigit} from "@ant-design/pro-components";
import {PostponedFormProps} from "@codingapi/ui-framework";


const PostponedFormView:React.FC<PostponedFormProps> = (props)=>{

    return (
        <ModalForm
            title={"延期调整"}
            open={props.visible}
            modalProps={{
                onCancel: () => {
                    props.setVisible(false);
                },
                destroyOnHidden:true,
            }}
            onFinish={async (values) => {
                props.onFinish(values.hours);
            }}
        >
            <ProFormDigit
                name={"hours"}
                label={"延期时间"}
                tooltip={"以当前时间开始延期，延期单位为小时"}
                addonAfter={"小时"}
                rules={[
                    {
                        required: true,
                        message: "请输入延期时间"
                    }
                ]}
            />
        </ModalForm>
    )
}

export default PostponedFormView;
