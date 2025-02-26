class RoleControl {


    /**
     * 存在所有角色
     * @param roles 角色列表
     */
    static hasRoles(roles: string[]): boolean {
        const authentications = this.roles();
        if (authentications) {
            for (let i = 0; i < roles.length; i++) {
                if (authentications.indexOf(roles[i]) === -1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 存在任意角色
     * @param roles 角色列表
     */
    static anyRoles(roles: string[]): boolean {
        const authentications = this.roles();
        if (authentications) {
            for (let i = 0; i < roles.length; i++) {
                if (authentications.indexOf(roles[i]) !== -1) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 不存在角色
     * @param roles 角色列表
     */
    static notHasRoles(roles: string[]): boolean {
        const authentications = this.roles();
        if (authentications) {
            for (let i = 0; i < roles.length; i++) {
                if (authentications.indexOf(roles[i]) !== -1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 不存在任意角色
     * @param roles 角色列表
     */
    static notAnyRoles(roles: string[]): boolean {
        const authentications = this.roles();
        if (authentications) {
            for (let i = 0; i < roles.length; i++) {
                if (authentications.indexOf(roles[i]) === -1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取角色列表
     */
    static roles(): string[] {
        const authorities = localStorage.getItem('authorities');
        if (authorities !== null && authorities !== undefined && authorities !== '' && authorities !== 'undefined') {
            return JSON.parse(authorities as string);
        } else {
            return [];
        }
    }

    /**
     * 不存在任何角色
     */
    static isNotRoles(): boolean {
        return this.roles().length === 0;
    }


}


export default RoleControl;
