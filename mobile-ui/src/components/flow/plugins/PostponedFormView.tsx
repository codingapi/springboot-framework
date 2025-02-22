import React from "react";
import {PostponedFormProps} from "@/components/flow/types";
import Popup from "@/components/popup";
import {DatePickerView} from "antd-mobile";
import dayjs from "dayjs";

const PostponedFormView: React.FC<PostponedFormProps> = (props) => {

    const [value, setValue] = React.useState(new Date());
    // 获取一小时后的日期
    const now = dayjs().add(1, 'hour').toDate();

    const labelRenderer = (type: string, data: number) => {
        switch (type) {
            case 'year':
                return data + '年'
            case 'month':
                return data + '月'
            case 'day':
                return data + '日'
            case 'hour':
                return data + '时'
            case 'minute':
                return data + '分'
            case 'second':
                return data + '秒'
            default:
                return data
        }
    }

    return (
        <Popup
            visible={props.visible}
            setVisible={props.setVisible}
            position='bottom'
            title={"延期截止日期"}
            bodyStyle={{height: '50vh'}}
            onOk={() => {
                const diff = dayjs(value).diff(dayjs(now), 'hour') + 1;
                props.onFinish(diff);
            }}
        >
            <div>
                <DatePickerView
                    precision={"hour"}
                    renderLabel={labelRenderer}
                    value={value}
                    min={now}
                    onChange={(value) => {
                        setValue(value);
                    }}
                />
            </div>
        </Popup>
    )
}

export default PostponedFormView;
