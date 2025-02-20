import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Slider} from "antd-mobile";
import formFieldInit from "@/components/form/common";

const FormSlider: React.FC<FormItemProps> = (props) => {

    const {formAction,rules,validateContext} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            rules={rules}
            hidden={props.hidden}
            help={props.help}
            disabled={props.disabled}
        >
            <Slider
                value={props.value}
                max={props.sliderMaxNumber}
                min={props.sliderMinNumber}
                step={props.sliderStep}
                range={props.sliderRange}
                ticks={props.sliderTicks}
                popover={props.sliderPopover}
                marks={props.sliderMarks}
                onChange={(e) => {
                    if(formAction) {
                        validateContext?.validateField(props.name, formAction);
                    }
                    props.onChange && props.onChange(e, formAction);
                }}
            />
        </Form.Item>
    )
}

export default FormSlider;
