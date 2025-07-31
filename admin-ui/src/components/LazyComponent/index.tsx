import React, {lazy, Suspense} from 'react';
import {Spin} from "antd";


interface LazyComponentProps {
    lazy:()=> Promise<{default: React.ComponentType<any>}>;
}

const LazyComponent:React.FC<LazyComponentProps> = (props) => {

    const PageComponent = lazy(() => {
        return props.lazy();
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

export default LazyComponent;

