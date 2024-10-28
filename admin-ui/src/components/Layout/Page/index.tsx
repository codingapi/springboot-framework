import React from "react";
import RoleKeyProvider from "@/framework/Permission/RoleKeyProvider";
import {PageContainer} from "@ant-design/pro-components";
import {PageContainerProps} from "@ant-design/pro-layout/es/components/PageContainer";
import AccessProvider from "@/framework/Permission/AccessProvider";


interface PageProps {
    children: React.ReactNode;
    enablePageContainer?: boolean;
    pageContainerProps?: PageContainerProps;
}


const Page: React.FC<PageProps> = (props) => {
    return (
        <RoleKeyProvider>
            <AccessProvider>
                {props.enablePageContainer && (
                    <PageContainer {...props.pageContainerProps}>
                        {props.children}
                    </PageContainer>
                )}
                {!props.enablePageContainer && (
                    <>
                        {props.children}
                    </>
                )}
            </AccessProvider>
        </RoleKeyProvider>
    )
}

export default Page;