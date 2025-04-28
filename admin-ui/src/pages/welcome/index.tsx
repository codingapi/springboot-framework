import React from 'react';
import {PageContainer} from "@ant-design/pro-components";
import './index.scss';


const WelcomePage = () => {

    return (
        <PageContainer>

            <ul>Admin-UI 支持的功能有</ul>
            <ul>1. 自定义流程</ul>
            <ul>2. 表单渲染</ul>
            <ul>3. 管理权限</ul>
            <ul>4. 动态菜单</ul>
            <ul>5. 动态加载组件</ul>
        </PageContainer>
    );
}

export default WelcomePage;
