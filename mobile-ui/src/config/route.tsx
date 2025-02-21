import Login from "@/pages/login";
import NotFound from "@/layout/NotFound";
import Layout from "@/layout";
import HomePage from "@/pages/home";
import {Route} from "react-router";
import {RouteObject} from "react-router/dist/lib/context";
import React from "react";
import EducationIndex from "@/pages/person/education";
import EducationForm from "@/pages/person/education/form";
import Test from "@/pages/test";
import LeaveListPage from "@/pages/levave";
import LeaveCreatePage from "@/pages/levave/create";
import FlowListPage from "@/pages/flow";
import FlowDetailPage from "@/pages/flow/detail";


export const routes: RouteObject[] = [
    {
        path: "/login",
        element: <Login/>,
    },
    {
        path: "/test",
        element: <Test/>,
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
                path: "/leave/index",
                element: <LeaveListPage/>,
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
                path: "/leave/create",
                element: <LeaveCreatePage/>,
            },
            {
                path: "/person/education/index",
                element: <EducationIndex/>,
            },
            {
                path: "/person/education/form",
                element: <EducationForm/>,
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
