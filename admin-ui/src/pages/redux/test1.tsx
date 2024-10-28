import React from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {decrement, increment} from '@/store/CounterSlice';
import {RootState} from "@/store/Redux";
import {Button, Space} from "antd";
import {PageContainer} from "@ant-design/pro-components";

const Test1 = () => {
    const counter = useSelector((state: RootState) => state.counter.value);
    const dispatch = useDispatch();

    return (
        <PageContainer>
            <div style={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
            }}>
                <div style={{
                    textAlign: 'center',
                    marginBottom: 20,
                }}>
                    <h1>Counter: {counter}</h1>
                </div>

                <Space>
                    <Button onClick={() => dispatch(increment())}>Increment</Button>
                    <Button onClick={() => dispatch(decrement())}>Decrement</Button>
                </Space>
            </div>
        </PageContainer>
    );
}

export default Test1;