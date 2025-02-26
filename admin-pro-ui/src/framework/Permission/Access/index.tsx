import React from "react";
import RoleControl from "@/utils/RoleControl";

export interface AccessProps {
    children?: React.ReactNode;

    hasRoles?: string[];
    hasAnyRoles?: string[];
    isNotRoles?: boolean;
    noRoles?: string[];
    noAnyRoles?: string[];
}

const Access: React.FC<AccessProps> = (props) => {
    const {hasRoles, hasAnyRoles, isNotRoles, noRoles, noAnyRoles} = props;
    if (hasRoles) {
        if (RoleControl.hasRoles(hasRoles)) {
            return (
                <>
                    {props.children}
                </>
            )
        }
    }

    if (hasAnyRoles) {
        if (RoleControl.anyRoles(hasAnyRoles)) {
            return (
                <>
                    {props.children}
                </>
            )
        }
    }

    if (isNotRoles) {
        if (RoleControl.isNotRoles()) {
            return (
                <>
                    {props.children}
                </>
            )
        }
    }

    if (noRoles) {
        if (RoleControl.notHasRoles(noRoles)) {
            return (
                <>
                    {props.children}
                </>
            )
        }
    }

    if (noAnyRoles) {
        if (RoleControl.notAnyRoles(noAnyRoles)) {
            return (
                <>
                    {props.children}
                </>
            )
        }
    }

    return null;
}

export default Access;