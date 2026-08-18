import React from "react";
import Login from "@/pages/login";

export const routes = [
    {
        path: '/login',
        element: <Login/>,
    },
]

export const menus = [
    {
        path: '/welcome',
        name: '欢迎',
        icon: "SmileOutlined",
        page: 'welcome',
    },
    {
        path: '/form',
        name: '表单',
        icon: "FormOutlined",
        page: 'form',
    },
    {
        path: '/mirco',
        name: '微前端',
        icon: "AppstoreOutlined",
        page: 'mirco',
    },
]


