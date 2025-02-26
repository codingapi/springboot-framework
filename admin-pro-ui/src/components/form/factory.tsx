import React from "react";
import {FormField} from "@/components/form/types";
import FormInput from "@/components/form/input";


class FormFactory {

    static create = (field: FormField) => {
        const type = field.type;
        const props = field.props;

        if (type === 'input') {
            return (
                <FormInput
                    {...props}
                    key={props.name}
                />
            )
        }

        return (
            <></>
        )
    }

}

export default FormFactory;
