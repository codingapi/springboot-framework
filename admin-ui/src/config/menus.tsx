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
        icon: "FormOutlined",
        page: 'mirco',
    },
    {
        path: '/flow',
        name: '流程',
        icon: "CiOutlined",
        routes: [
            {
                path: '/flow/work',
                name: '流程管理',
                page: 'flow/work',
            },
            {
                path: '/flow/record',
                name: '办理中心',
                page: 'flow/record',
            },
            {
                path: '/flow/leave',
                name: '请假管理',
                page: 'flow/leave',
            },
            {
                path: '/flow/user',
                name: '流程用户',
                page: 'flow/user',
            },
        ]
    },
]


