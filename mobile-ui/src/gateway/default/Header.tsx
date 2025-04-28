import React from "react";
import {HeaderProps} from "@/gateway";


const HeaderDefault: React.FC<HeaderProps> = (props) => {
    return (
        <div>
            <h1>{props.title}</h1>
            <button onClick={props.onClick}>Click Me</button>
            <div>
                <span>PS:local default component</span>
            </div>
        </div>
    )
}

export default HeaderDefault;
