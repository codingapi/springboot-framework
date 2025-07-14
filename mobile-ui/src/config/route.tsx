import Login from "@/pages/login";
import NotFound from "@/layout/NotFound";
import Layout from "@/layout";
import HomePage from "@/pages/home";
import {Route} from "react-router";
import {RouteObject} from "react-router/dist/lib/context";
import React from "react";
import LeaveListPage from "@/pages/leave";
import LeaveCreatePage from "@/pages/leave/create";
import FlowListPage from "@/pages/flow";
import FlowDetailPage from "@/pages/flow/detail";
import LeaveDetailPage from "@/pages/leave/detail";
import FormPage from "@/pages/form";
import MircoPage from "@/pages/mirco";


export const routes: RouteObject[] = [
    {
        path: "/login",
        element: <Login/>,
    },
    {
        path: '/',
        element: <Layout/>,
        children: [
            {
                path: "/",
                element: <HomePage/>,
            },
            {
                path: "/form",
                element: <FormPage/>,
            },
            {
                path: "/mirco",
                element: <MircoPage/>,
            },
            {
                path: "/leave/index",
                element: <LeaveListPage/>,
            },
            {
                path: "/leave/create",
                element: <LeaveCreatePage/>,
            },
            {
                path: "/leave/detail",
                element: <LeaveDetailPage/>,
            },
            {
                path: "/flow/list",
                element: <FlowListPage/>,
            },
            {
                path: "/flow/detail",
                element: <FlowDetailPage/>,
            },
            {
                path: '/*',
                element: <NotFound/>,
            }
        ]
    },
];

const loadRoutes = (routes: RouteObject[]) => {
    return (
        <>
            {routes.map((route) => {
                return (
                    <Route
                        key={route.path}
                        path={route.path}
                        element={route.element}
                        children={route.children && loadRoutes(route.children)}
                    />
                )
            })}
        </>
    )
}


export const loadLayoutRoutes = () => {
    const rootRoutes = routes
        .filter((route) => route.path === '/');
    if (rootRoutes && rootRoutes.length > 0) {
        const list = rootRoutes[0].children;
        if (list) {
            return loadRoutes(list);
        }
        return []
    }
    return (
        <></>
    )
}
