import RoleControl from "@/utils/RoleControl";

interface AccessHandler {
    match: (child: any) => boolean;
    handle: (child: any) => any;
}


class HasRolesHandler implements AccessHandler {

    authorize = 'has-roles';

    match(child: any): boolean {
        return child.props && child.props[this.authorize];
    }

    handle(child: any): any {
        const roles = child.props[this.authorize];
        if (RoleControl.hasRoles(roles)) {
            return child;
        }
        return null;
    }
}

class AnyRolesHandler implements AccessHandler {

    authorize = 'has-any-roles';

    match(child: any): boolean {
        return child.props && child.props[this.authorize];
    }

    handle(child: any): any {
        const roles = child.props[this.authorize];
        if (RoleControl.anyRoles(roles)) {
            return child;
        }
        return null;
    }
}

class NotRolesHandler implements AccessHandler {

    authorize = 'not-roles';

    match(child: any): boolean {
        return child.props && child.props[this.authorize];
    }

    handle(child: any): any {
        if (RoleControl.isNotRoles()) {
            return child;
        }
        return null;
    }
}

class NoRolesHandler implements AccessHandler {

    authorize = 'no-roles';

    match(child: any): boolean {
        return child.props && child.props[this.authorize];
    }

    handle(child: any): any {
        const roles = child.props[this.authorize];
        if (RoleControl.notHasRoles(roles)) {
            return child;
        }
        return null;
    }
}

class NoAnyRolesHandler implements AccessHandler {

    authorize = 'no-any-roles';

    match(child: any): boolean {
        return child.props && child.props[this.authorize];
    }

    handle(child: any): any {
        const roles = child.props[this.authorize];
        if (RoleControl.notAnyRoles(roles)) {
            return child;
        }
        return null;
    }
}


export const accessHandlers: AccessHandler[] = [
    new HasRolesHandler(),
    new NoRolesHandler(),
    new AnyRolesHandler(),
    new NoAnyRolesHandler(),
    new NotRolesHandler()
];


