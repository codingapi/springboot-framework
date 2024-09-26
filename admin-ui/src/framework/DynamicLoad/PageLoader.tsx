import React, {lazy, Suspense} from 'react';
import {Spin} from "antd";

export const loadPage = (pageName: string) => {

    const PageComponent = lazy(() => {
        return import(`@/pages/${pageName}`);
    });

    const fallback = (
        <Spin size="large" tip="Loading">
            <div style={{minHeight: 100}}/>
        </Spin>
    );

    return (
        <Suspense fallback={fallback}>
            <PageComponent/>
        </Suspense>
    );
};
