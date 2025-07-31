import React, {lazy, Suspense} from 'react';
import {Loading} from "antd-mobile";


interface LazyComponentProps {
    lazy:()=> Promise<{default: React.ComponentType<any>}>;
}

const LazyComponent:React.FC<LazyComponentProps> = (props) => {

    const PageComponent = lazy(() => {
        return props.lazy();
    });

    return (
        <Suspense fallback={<Loading />}>
            <PageComponent/>
        </Suspense>
    );
};

export default LazyComponent;

