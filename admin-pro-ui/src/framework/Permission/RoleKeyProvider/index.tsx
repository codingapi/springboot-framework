import React from "react";
import RoleKeyManager from "@/framework/Permission/RoleKeyProvider/RoleKeyManager";

interface RoleKeyProviderProps {
    children: React.ReactNode;
}

const renderWithAccess = (child: any): any => {
    if (!React.isValidElement(child)) {
        return child;
    }
    if (child.props) {
        // @ts-ignore
        if (child.props.children) {
            child = React.cloneElement(child, {
                ...child.props,
                // @ts-ignore
                children: React.Children.map(child.props.children, (item: any) => {
                    return renderWithAccess(item);
                })
            });
        }

        const roleKey = 'role-key';
        const key = child.props && child.props[roleKey];
        if(key) {
            if (RoleKeyManager.getInstances().hasRole(key)) {
                return child;
            }else {
                return null;
            }
        }
        return child;
    }
    return null;
};

const RoleKeyProvider: React.FC<RoleKeyProviderProps> = (props) => {
    const {children} = props;
    return React.Children.map(children, child => renderWithAccess(child));
};

export default RoleKeyProvider;