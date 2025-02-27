import React, {useEffect} from "react";
import {UserSelectProps} from "@/components/flow/flow/types";
import {ModalForm, ProForm, ProFormSelect} from "@ant-design/pro-components";
import {users} from "@/api/user";


const UserSelectView: React.FC<UserSelectProps> = (props) => {

    const [form] = ProForm.useForm();

    const [userList, setUserList] = React.useState<any[]>([]);

    useEffect(() => {
        users().then((res) => {
            if (res.success) {
                const list = res.data.list.filter((item:any)=>{
                    const specifyUserIds = props.specifyUserIds;
                    if(specifyUserIds && specifyUserIds.length > 0){
                        return specifyUserIds.includes(item.id);
                    }
                });
                setUserList(list);
                // 默认选中当前用户
                form.setFieldValue("users", props.currentUserIds);
            }
        })
    }, []);

    return (
        <ModalForm
            form={form}
            open={props.visible}
            title={"用户选择（模拟测试）"}
            modalProps={{
                onCancel: () => {
                    props.setVisible(false);
                },
                onClose: () => {
                    props.setVisible(false);
                }
            }}
            onFinish={async (values) => {
                const users = values.users;
                const selectedUsers = userList.filter((item: any) => {
                    return users.includes(item.id);
                });
                props.onFinish(selectedUsers);
                props.setVisible(false);
            }}
        >
            <ProFormSelect
                mode={"tags"}
                name={"users"}
                label={"用户"}
                options={userList.map((item: any) => {
                    return {
                        label: item.name,
                        value: item.id
                    }
                })}
            />
        </ModalForm>
    )
}

export default UserSelectView;
