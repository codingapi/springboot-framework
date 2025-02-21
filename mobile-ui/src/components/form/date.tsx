import React from "react";
import {FormItemProps} from "@/components/form/types";
import {DatePicker, Form} from "antd-mobile";
import {RightOutline} from "antd-mobile-icons";
import dayjs from "dayjs";
import "./form.scss";
import formFieldInit from "@/components/form/common";

const FormDate: React.FC<FormItemProps> = (props) => {

    const {formAction, rules} = formFieldInit(props);

    const format = props.dateFormat || 'YYYY-MM-DD';
    const precision = props.datePrecision || "day";
    const [visible, setVisible] = React.useState(false);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            rules={rules}
            hidden={props.hidden}
            help={props.help}
            disabled={props.disabled}
            extra={(
                <RightOutline
                    onClick={() => {
                        setVisible(true);
                    }}
                />
            )}
            getValueProps={(value) => {
                if (value) {
                    return {
                        value: dayjs(value).toDate()
                    }
                }
                return value;
            }}
        >
            <DatePicker
                title={props.label}
                value={props.value}
                visible={visible}
                precision={precision}
                onClose={() => {
                    setVisible(false)
                }}
                onConfirm={value => {
                    const currentDate = dayjs(value).format(format);
                    formAction?.setFieldValue(props.name as string, currentDate);
                    props.onChange && props.onChange(currentDate, formAction);
                    setVisible(false)
                }}
            >
                {value => {
                    if (value) {
                        const currentValue = dayjs(value).format(format);
                        return (
                            <span
                                onClick={() => {
                                    setVisible(true)
                                }}
                            >{currentValue}</span>
                        )
                    }
                    return (
                        <span
                            onClick={() => {
                                setVisible(true)
                            }}
                            className={"placeholder-span"}
                        >{props.placeholder || '请选择日期'}
                        </span>
                    )
                }}
            </DatePicker>
        </Form.Item>
    )
}

export default FormDate;
