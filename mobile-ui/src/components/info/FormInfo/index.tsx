import React from "react";
import Index, {FormProps} from "@/components/form";
import "./index.scss";

interface FormInfoProps extends FormProps {
    title: string;
}

const FormInfo: React.FC<FormInfoProps> = (props) => {
    return (
        <div className={"form-info"}>
            <div className={"form-header-title"}>{props.title}</div>
            <div className={"form-content"}>
                <Index {...props}/>
            </div>
        </div>
    )
}

export default FormInfo;
