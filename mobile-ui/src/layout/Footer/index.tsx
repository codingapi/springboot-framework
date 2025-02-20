import React from "react";
import "./index.scss";

interface FooterLayoutProps {
    children?: React.ReactNode;
}

const FooterLayout: React.FC<FooterLayoutProps> = (props) => {

    return (
        <div
            className={"page-footer"}
        >
            {props.children}
        </div>
    )
}

export default FooterLayout;
