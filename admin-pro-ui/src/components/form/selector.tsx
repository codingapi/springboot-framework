import React from "react";
import {FormItemProps} from "@/components/form/types";
import FormCheckbox from "@/components/form/checkbox";
import "./form.scss";

const FormSelector: React.FC<FormItemProps> = (props) => {
    return (
        <FormCheckbox {...props} />
    )
}

export default FormSelector;
