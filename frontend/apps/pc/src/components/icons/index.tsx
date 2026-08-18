import React from 'react';
import Icon, * as icons from '@ant-design/icons'

interface MenuIconProps {
    icon: string
    className?: string;
    style?: React.CSSProperties;
    onClick?: () => void;
}

const Icons: React.FC<MenuIconProps> = (props) => {
    if (props.icon === '-') {
        return <></>
    }

    // @ts-ignore
    const component = icons[props.icon];
    if (props.icon) {
        return (
            <Icon
                onClick={props.onClick}
                className={props.className}
                style={props.style}
                component={component}
            />
        )
    } else {
        return <></>
    }
}
export default Icons;
