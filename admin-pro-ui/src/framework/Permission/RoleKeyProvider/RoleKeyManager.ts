interface RoleKey {
    key: string;
    children?: RoleKey[];
}

class RoleKeyManager {
    private roles: RoleKey[] = [];

    private static manager = new RoleKeyManager();

    static getInstances(): RoleKeyManager {
        return RoleKeyManager.manager;
    }

    addRole(role: RoleKey): void {
        this.roles.push(role);
    }

    public hasRole(code: string) {
        return this.roles.some((role: RoleKey) => {
            return role.key === code;
        })
    }
}

export default RoleKeyManager;