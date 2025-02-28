import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form} from "antd";
import formFieldInit from "@/components/form/common";
import "./form.scss";
import CodeEditor from "@/components/CodeEditor";


const FormCode: React.FC<FormItemProps> = (props) => {

    const {formAction} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            hidden={props.hidden}
            help={props.help}
            required={props.required}
        >
            <CodeEditor
                readonly={props.disabled}
                value={props.value}
                onChange={(value) => {
                    formAction?.setFieldValue(props.name, value);
                    props.onChange && props.onChange(value, formAction);
                }}
                theme={props.codeTheme}
                language={props.codeLanguage}
                fontSize={props.codeFontSize}
                style={props.codeStyle}
                actionRef={props.codeActionRef}
                onSelectedRun={props.onCodeSelectedRun}
            />
        </Form.Item>
    )
}

export default FormCode;
