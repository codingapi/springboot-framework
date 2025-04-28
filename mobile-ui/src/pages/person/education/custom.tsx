import React from "react";
import {SelectOptionFormEditProps} from "@codingapi/ui-framework";
import {FormInput,Form} from "@codingapi/form-mobile";

const CustomFormEditOption:React.FC<SelectOptionFormEditProps> = (props)=>{
    return (
        <Form
            form={props.currentInstance}
        >
            <FormInput
                name={"type"}
                label={"学科类别"}
                placeholder={"请输入学科类别"}
            />
        </Form>
    )
}

export default CustomFormEditOption;
