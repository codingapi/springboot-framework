import React from "react";
import {FormItemProps} from "@/components/form/types";
import {DatePicker, Form, Space} from "antd";
import dayjs from "dayjs";
import formFieldInit from "@/components/form/common";
import "./form.scss";
import {FormAction} from "@/components/form/index";

const datePrecisionConverter = (precision?: string) => {
    if (precision === "day") {
        return "date";
    }
    if (precision === "year") {
        return "year";
    }
    if (precision === "month") {
        return "month";
    }
    if (precision === "week") {
        return "week";
    }
    if (precision === "quarter") {
        return "quarter";
    }
    if (precision === "week-day") {
        return "week";
    }

    if (precision === "hour") {
        return "date";
    }

    if (precision === "minute") {
        return "date";
    }

    if (precision === "second") {
        return "date";
    }

    return null;
}

const showTime = (precision?: string) => {
    if (precision === "hour") {
        return {
            format: 'HH'
        }
    }

    if (precision === "minute") {
        return {
            format: 'HH:mm'
        }
    }

    if (precision === "second") {
        return {
            format: 'HH:mm:ss'
        }
    }

    return null;
}

interface $DatePicker extends FormItemProps{
    formAction?:FormAction;
}

const $DatePicker:React.FC<$DatePicker> = (props)=>{

    const formAction = props.formAction;

    const format = props.dateFormat || 'YYYY-MM-DD';
    const precision = datePrecisionConverter(props.datePrecision) || "date";
    const showTimeConfig = showTime(props.datePrecision);

    return (
      <Space.Compact
          style={{
              width:"100%"
          }}
      >
          {props.addonBefore}
          <DatePicker
              style={{
                  width:"100%"
              }}
              disabled={props.disabled}
              value={props.value}
              prefix={props.prefix}
              suffixIcon={props.suffix}
              picker={precision}
              showTime={showTimeConfig?{format: showTimeConfig.format}:false}
              onChange={(date, dateString) => {
                  const currentDate = dayjs(date).format(format);
                  formAction?.setFieldValue(props.name, currentDate);
                  props.onChange && props.onChange(currentDate, formAction);
              }}
          />
          {props.addonAfter}
      </Space.Compact>
    )
}

const FormDate: React.FC<FormItemProps> = (props) => {

    const {formAction} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            hidden={props.hidden}
            help={props.help}
            required={props.required}
            tooltip={props.tooltip}
            getValueProps={(value) => {
                if (value) {
                    return {
                        value: dayjs(value)
                    }
                }
                return value;
            }}
        >
            <$DatePicker
                {...props}
                formAction={formAction}
            />

        </Form.Item>
    )
}

export default FormDate;
