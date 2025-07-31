import NotFound from "@/layout/NotFound";
import {Route} from "react-router";
import {RouteObject} from "react-router/dist/lib/context";
import React from "react";
import LazyComponent from "@/components/LazyComponent";

export const routes: RouteObject[] = [
    {
        path: "/login",
        element: (
            <LazyComponent
                lazy={() => {
                    return import('@/pages/login');
                }}
            />
        ),
    },
    {
        path: '/',
        element: (
            <LazyComponent
                lazy={() => {
                    return import('@/layout/index');
                }}
            />
        ),
        children: [
            {
                path: "/",
                element: (
                    <LazyComponent
                        lazy={() => {
                            return import('@/pages/home');
                        }}
                    />
                ),
            },
            {
                path: "/form",
                element: (
                    <LazyComponent
                        lazy={() => {
                            return import('@/pages/form');
                        }}
                    />
                ),
            },
            {
                path: "/mirco",
                element: (
                    <LazyComponent
                        lazy={() => {
                            return import('@/pages/mirco');
                        }}
                    />
                ),
            },
            {
                path: "/leave/index",
                element: (
                    <LazyComponent
                        lazy={() => {
                            return import('@/pages/leave');
                        }}
                    />
                ),
            },
            {
                path: "/leave/create",
                element: (
                    <LazyComponent
                        lazy={() => {
                            return import('@/pages/leave/create');
                        }}
                    />
                ),
            },
            {
                path: "/leave/detail",
                element: (
                    <LazyComponent
                        lazy={() => {
                            return import('@/pages/leave/detail');
                        }}
                    />
                ),
            },
            {
                path: "/flow/list",
                element: (
                    <LazyComponent
                        lazy={() => {
                            return import('@/pages/flow');
                        }}
                    />
                ),
            },
            {
                path: "/flow/detail",
                element: (
                    <LazyComponent
                        lazy={() => {
                            return import('@/pages/flow/detail');
                        }}
                    />
                ),
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
