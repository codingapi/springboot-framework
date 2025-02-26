import React from "react";
import {SelectOptionFormEditProps} from "@/components/form/types";
import Form from "@/components/form";
import FormInput from "@/components/form/input";

const CustomFormEditOption:React.FC<SelectOptionFormEditProps> = (props)=>{
    return (
        <Form
            actionRef={props.formAction}
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
