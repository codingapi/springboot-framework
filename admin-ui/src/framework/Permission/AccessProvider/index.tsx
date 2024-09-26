import React from "react";
import {accessHandlers} from "@/framework/Permission/AccessProvider/handler";

interface AccessProviderProps {
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

        for (let i = 0; i < accessHandlers.length; i++) {
            if (accessHandlers[i].match(child)) {
                return accessHandlers[i].handle(child);
            }
        }

        return child;
    }
    return null;
};

const AccessProvider: React.FC<AccessProviderProps> = (props) => {
    const {children} = props;
    return React.Children.map(children, child => renderWithAccess(child));
};

export default AccessProvider;