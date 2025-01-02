import React, {useEffect, useState} from "react";
import {Provider, useDispatch} from "react-redux";
import {salaryStore, updateUsers} from "@/pages/salary/store/salary";
import {PageContainer} from "@ant-design/pro-components";
import {Tabs} from "antd";
import SalaryTable1 from "@/pages/salary/compoments/SalaryTable1";
import SalaryTable2 from "@/pages/salary/compoments/SalaryTable2";
import {users} from "@/api/salary";


const $Salary = () => {

    const items = [
        {
            key: "1",
            label: "总科目",
            children: <SalaryTable1/>
        },
    ];

    for (let i = 2; i < 500; i++) {
        items.push(
            {
                key: i + '',
                label: "绩效工资",
                children: (
                    <SalaryTable2/>
                )
            })
    }

    const [activeKey, setActiveKey] = useState("1");

    const dispatch = useDispatch();

    useEffect(() => {
        users().then(res => {
            dispatch(updateUsers(res));
        })
    }, []);

    return (
        <PageContainer>
            <Tabs
                activeKey={activeKey}
                onChange={(value) => {
                    setActiveKey(value);
                }}
            >
                {items.map(item => {
                    return (
                        <Tabs.TabPane
                            key={item.key}
                            tab={item.label}
                        />
                    )
                })}
            </Tabs>

            {items.map(item => {
                if (item.key === activeKey) {
                    return item.children;
                }
            })}


        </PageContainer>
    )
}

const Salary = () => {
    return (
        <>
            <Provider store={salaryStore}>
                <$Salary/>
            </Provider>
        </>
    )
}


export default Salary;
