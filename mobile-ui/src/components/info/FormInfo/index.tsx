import React from "react";
import {Form} from "@codingapi/form-mobile";
import "./index.scss";
import {FormProps} from "@codingapi/ui-framework";

interface FormInfoProps extends FormProps {
    title: string;
}

const FormInfo: React.FC<FormInfoProps> = (props) => {
    return (
        <div className={"form-info"}>
            <div className={"form-header-title"}>{props.title}</div>
            <div className={"form-content"}>
                <Form {...props}/>
            </div>
        </div>
    )
}

export default FormInfo;
