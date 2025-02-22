import React from "react";
import {UserSelectFormProps} from "@/components/flow/types";
import Popup from "@/components/popup";

const UserSelectFormView: React.FC<UserSelectFormProps> = (props) => {

    return (
        <Popup
            visible={props.visible}
            setVisible={props.setVisible}
            position='bottom'
            title={"选人人员"}
            bodyStyle={{height: '50vh'}}
            onOk={() => {

            }}
        >
            <div>

            </div>
        </Popup>
    )
}

export default UserSelectFormView;
