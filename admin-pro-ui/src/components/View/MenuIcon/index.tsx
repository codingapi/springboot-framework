import React from 'react';
import Icon, * as icons from '@ant-design/icons'


interface MenuIconProps {
    icon: string
    className?: string;
    style?: React.CSSProperties;
    onClick?: () => void;
}

const MenuIcon: React.FC<MenuIconProps> = (props) => {
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

export async function loadLayoutMenus(response: any) {
    if (response.success) {
        let children = response.data.children;
        if (children === null) {
            return [];
        }
        const fetchMenu = (data: any) => {
            data.icon = <MenuIcon icon={data.icon}/>;
            data.children = data.children || [];
            data.children.forEach((item: any) => {
                fetchMenu(item);
            });
            return data;
        }
        children = children.map((item: any) => fetchMenu(item));
        return children;
    } else {
        return [];
    }
}

export async function loadLayoutMenuAuthentications(response: any) {
    if (response.success) {
        let children = response.data.children;
        if (children === null) {
            return [];
        }
        const authorities: string[] = [];
        const fetchAuthorities = (data: any) => {
            authorities.push(data.path);
            if (data.children) {
                data.children.forEach((item: any) => {
                    fetchAuthorities(item);
                });
            }
        }
        children.forEach((element: any) => {
            fetchAuthorities(element);
        });
        return authorities;
    }
    return [];
}

export default MenuIcon;