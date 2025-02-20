import React from "react";
import {Footer} from "antd-mobile";

interface FooterLayoutProps {
    children?: React.ReactNode;
}

const FooterLayout: React.FC<FooterLayoutProps> = (props) => {

    return (
        <Footer
            content={props.children}
        />
    )
}

export default FooterLayout;
