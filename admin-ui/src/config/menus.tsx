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
        path: '/role',
        name: '权限',
        icon: "SmileOutlined",
        page: 'role',
    },
    {
        path: '/menu',
        name: '菜单',
        icon: "MenuOutlined",
        roles: ['ROLE_ADMIN'],
        routes: [
            {
                path: '/menu/index',
                name: '菜单管理',
                page: 'menu',
            },
        ],
    },
    {
        path: '/redux',
        name: '状态管理',
        icon: "CrownFilled",
        routes: [
            {
                path: '/redux/test1',
                name: '测试页面1',
                page: 'redux/test1',
                roles: ['ROLE_ADMIN'],
            },
            {
                path: '/redux/test2',
                name: '测试页面2',
                page: 'redux/test2',
            },
        ],
    },
    {
        path: '/dynamic',
        name: '动态加载',
        icon: "CrownFilled",
        roles: ['ROLE_ADMIN'],
        routes: [
            {
                path: '/dynamic/test1',
                name: '动态加载1',
                page: 'dynamic/test1',
            },
            {
                path: '/dynamic/test2',
                name: '动态加载2',
                page: 'dynamic/test2',
            },
        ],
    },
]


