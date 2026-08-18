import React from "react";
import {NavBar} from "antd-mobile";
import {useNavigate} from "react-router";
import "./index.scss";

interface HeaderLayoutProps{
    children: React.ReactNode;
    isHome?:boolean;
    left?:React.ReactNode;
    right?:React.ReactNode;
}

const HeaderLayout:React.FC<HeaderLayoutProps> = (props)=>{
    const navigate = useNavigate();

    const back = ()=>{
        navigate(-1);
    }

    return (
        <NavBar
            className={"page-header"}
            onBack={back}
            backIcon={props.isHome?false:undefined}
            left={props.left}
            right={props.right}
        >
            {props.children}
        </NavBar>
    )
}

export default HeaderLayout;
