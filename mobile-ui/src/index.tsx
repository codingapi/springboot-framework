import React, {createContext} from 'react';
import ReactDOM from 'react-dom/client';
import reportWebVitals from './reportWebVitals';
import './index.scss';
import {createHashRouter, RouterProvider} from "react-router-dom";
import {routes} from "@/config/route";
import zhCN from "antd-mobile/es/locales/zh-CN";
import {ConfigProvider} from "antd-mobile";
import "@/config/register.component";

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);

const RouteContext = createContext(null);

root.render(
    <ConfigProvider locale={zhCN}>
        <RouteContext.Provider
            value={null}
        >
            <RouterProvider router={createHashRouter(routes)}/>
        </RouteContext.Provider>
    </ConfigProvider>
);

// If you want to start measuring performance in your app, pass a function
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
