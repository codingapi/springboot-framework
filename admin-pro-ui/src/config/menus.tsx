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
    }
]


