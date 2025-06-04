import React, {useEffect} from "react";
import {UserSelectFormProps} from "@codingapi/ui-framework";
import {ModalForm, ProForm, ProFormSelect} from "@ant-design/pro-components";

const users = async () => {
    const data = [];
    for (let i = 0; i < 500; i++) {
        data.push({
            id: i,
            name: `张三${i}`
        });
    }
    return data
}


const UserSelectView: React.FC<UserSelectFormProps> = (props) => {

    const [form] = ProForm.useForm();

    const [userList, setUserList] = React.useState<any[]>([]);

    useEffect(() => {
        users().then((userList) => {
            const list = userList.filter((item: any) => {
                const specifyUserIds = props.specifyUserIds;
                if (specifyUserIds && specifyUserIds.length > 0) {
                    return specifyUserIds.includes(item.id);
                }
            });
            setUserList(list);
            // 默认选中当前用户
            form.setFieldValue("users", props.currentUserIds);
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
                destroyOnHidden:true,
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
