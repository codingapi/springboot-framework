import {ThemeConfig} from "antd";

export const theme = {
    token: {
        colorPrimary: '#4a79d8',
    }
} as ThemeConfig;


export const config = {
    // 主题配置
    theme: theme,
    // 后台名称
    title: 'Admin UI',
    // 后台logo
    logo: '/logo.png',
    // 欢迎页路径
    welcomePath: '/welcome',
    // 登录页路径
    loginPath: '/login',
    // 侧边栏宽度
    siderWidth: 216,
    // 侧边栏布局
    layout: 'mix' as 'side' | 'top' | 'mix',
    // 水印
    waterMark: 'default waterMark',
}

