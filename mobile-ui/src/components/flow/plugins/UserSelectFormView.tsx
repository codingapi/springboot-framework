import React, {useEffect} from "react";
import {UserSelectFormProps} from "@/components/flow/types";
import Popup from "@/components/popup";
import Form, {FormAction} from "@/components/form";
import FormInput from "@/components/form/input";

const UserSelectFormView: React.FC<UserSelectFormProps> = (props) => {

    const formAction = React.useRef<FormAction>(null);

    useEffect(() => {
        if(props.visible){
            if(props.specifyUserIds){
                formAction.current?.setFieldValue("users", props.specifyUserIds.join(","));
            }
        }
    }, [props.visible]);

    return (
        <Popup
            visible={props.visible}
            setVisible={props.setVisible}
            position='bottom'
            title={"选人人员"}
            bodyStyle={{height: '50vh'}}
            onOk={() => {
                const users = formAction.current?.getFieldValue("users");
                if(users){
                    const userIds = Array.of(...users.split(",")).map(item =>{
                        return {
                            id: item,
                            name: item
                        }
                    });
                    props.onFinish(userIds);
                }
            }}
        >
            <div>
                <Form
                    actionRef={formAction}
                >
                    <FormInput
                        name={"users"}
                        label={"人员"}
                        placeholder={"请选择人员"}
                    />
                </Form>

            </div>
        </Popup>
    )
}

export default UserSelectFormView;
