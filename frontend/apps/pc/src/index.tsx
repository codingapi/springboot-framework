import React from 'react';
import ReactDOM from 'react-dom/client';
import reportWebVitals from './reportWebVitals';
import RoutesProvider from "@/framework/Routes/RoutesProvider";
import {Provider} from "react-redux";
import store from "@/store/Redux";
import {ConfigProvider} from "antd";
import zhCN from 'antd/es/locale/zh_CN';
import '@/styles/index.scss';
import("@/config/register.component").then(()=>{
    console.log('register.component loaded');
});
import {CSSUtils, ThemeConfig, ThemeProvider} from "@codingapi/ui-framework";

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);

export const theme = {
    token: {
        colorPrimary: CSSUtils.getRootVariable('--primary-color'),
        contentFontSize: CSSUtils.getRootVariable('--content-font-size'),
    }
} as ThemeConfig;


root.render(
    <React.StrictMode>
        <ThemeProvider theme={theme}>
            <ConfigProvider
                locale={zhCN}
                theme={theme}
            >
                <Provider store={store}>
                    <RoutesProvider/>
                </Provider>
            </ConfigProvider>
        </ThemeProvider>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
