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
        path: '/user',
        name: '用户列表',
        icon: "UserOutlined",
        page: 'user',
    },
    {
        path: '/jar',
        name: '权限',
        icon: "SmileOutlined",
        page: 'jar',
    },
    {
        path: '/flow',
        name: '流程',
        icon: "SmileOutlined",
        routes: [
            {
                path: '/flow/work',
                name: '流程管理',
                icon: "SmileOutlined",
                page: 'flow/work',
            },
            {
                path: '/flow/record',
                name: '办理中心',
                icon: "SmileOutlined",
                page: 'flow/record',
            },
            {
                path: '/flow/leave',
                name: '请假管理',
                icon: "SmileOutlined",
                page: 'flow/leave',
            },
            {
                path: '/flow/user',
                name: '流程用户',
                icon: "SmileOutlined",
                page: 'flow/user',
            },
        ]
    },
    {
        path: '/node',
        name: '节点管理',
        icon: "NodeIndexOutlined",
        page: 'node',
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


