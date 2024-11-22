import React, {useEffect} from 'react';
import logo from '@/assets/logo.svg';
import './index.scss';
import {useSelector} from "react-redux";
import {RootState} from "@/store/Redux";
import RoleControl from "@/utils/RoleControl";
import Page from "@/components/Layout/Page";

const Index = () => {

    const counter = useSelector((state: RootState) => state.counter.value);
    const username = localStorage.getItem('username');

    useEffect(() => {
        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible') {
                console.log('Tab is active');
                // 执行页面重新激活时的操作
            } else {
                console.log('Tab is inactive');
                // 执行页面被隐藏时的操作
            }
        };

        // 监听 visibilitychange 事件
        document.addEventListener('visibilitychange', handleVisibilityChange);

        // 清理事件监听器
        return () => {
            document.removeEventListener('visibilitychange', handleVisibilityChange);
        };
    }, []);

    return (
        <Page enablePageContainer={true}>
            <div className="App">
                <header className="App-header">
                    <img src={logo} className="App-logo" alt="logo"/>
                    <p>
                        hi {username} , Redux counter: {counter}, Roles: {RoleControl.roles().map(item => (
                        <label
                            key={item}
                            style={{
                                margin: '0 5px',
                                padding: '5px',
                            }}>{item}</label>
                    ))}
                    </p>
                </header>
            </div>
        </Page>
    );
}

export default Index;
