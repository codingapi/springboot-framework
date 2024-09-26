import {ActionType, ProLayout} from '@ant-design/pro-components';
import React, {useEffect, useRef, useState} from 'react';
import {Route, Routes} from "react-router";
import {useNavigate} from "react-router-dom";
import {MenuRouteManager} from "@/framework/Routes/MenuRouteManager";
import AvatarHeader from "@/components/Layout/AvatarHeader";
import {loadHeaderAction} from "@/components/Layout/HeaderAction";
import {config} from "@/config/theme";
import "./index.scss";
import {useSelector} from "react-redux";
import {RootState} from "@/store/Redux";
import NotFount from "@/layout/pages/NotFount";

const welcomePath = config.welcomePath;
const loginPath = config.loginPath;

const Layout = () => {

    const actionRef = useRef<ActionType>();
    const [pathname, setPathname] = useState(welcomePath);

    const menuVersion = useSelector((state: RootState) => state.menu.version);

    const navigate = useNavigate();

    const username = localStorage.getItem('username');

    useEffect(() => {
        const path = window.location.hash.replace('#', '') || welcomePath;
        setPathname(path);
        MenuRouteManager.getInstance().refresh();
        actionRef.current?.reload();
    }, [menuVersion]);


    return (
        <ProLayout
            siderWidth={config.siderWidth}
            layout={config.layout}
            location={{
                pathname,
            }}
            title={config.title}
            logo={config.logo}
            actionRef={actionRef}
            waterMarkProps={{
                content: username || config.waterMark,
            }}
            menu={{
                request: async () => {
                    return MenuRouteManager.getInstance().getMenus();
                }
            }}
            breadcrumbProps={{
                itemRender: (route: any, params, routes, paths) => {
                    return (
                        <label
                            className={"breadcrumb-item"}
                            onClick={() => {
                                return;
                            }}
                        >
                            {route.breadcrumbName}
                        </label>
                    );
                }
            }}
            avatarProps={{
                render: (props, defaultDom) => {
                    return (
                        <AvatarHeader props={props}/>
                    )
                }
            }}
            actionsRender={(props) => {
                return loadHeaderAction(props);
            }}
            onPageChange={(location:any) => {
                const token = localStorage.getItem('token');
                if (!token) {
                    navigate(loginPath, {replace: true});
                }else{
                    navigate(location.pathname);
                }
            }}
            menuItemRender={(item, dom) => (
                <div
                    onClick={() => {
                        const currentPath = item.path || welcomePath
                        setPathname(currentPath);
                        navigate(currentPath);
                    }}
                >
                    {dom}
                </div>
            )}
        >
            <Routes>
                {MenuRouteManager.getInstance().getRoutes()}
                <Route path={"/*"} key={"404"} element={<NotFount/>}/>
            </Routes>
        </ProLayout>
    );
};

export default Layout;
