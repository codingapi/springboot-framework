import type {HttpClient, Response} from "@codingapi/ui-framework";

export interface UserAccount {
    username: string;
    token: string;
    authorities?: string[];
    avatar?: string;
    [key: string]: any;
}

export interface AccountApi {
    login: (body: any) => Promise<Response>;
    captcha: () => Promise<Response>;
    initUser: (user: UserAccount) => void;
    clearUser: () => void;
}

export const createAccountApi = (httpClient: HttpClient): AccountApi => {

    async function login(body: any) {
        return httpClient.post('/user/login', body);
    }

    async function captcha() {
        return httpClient.get('/open/captcha');
    }

    function clearUser() {
        localStorage.removeItem('username');
        localStorage.removeItem('token');
        localStorage.removeItem('authorities');
        localStorage.removeItem('avatar');
    }

    function initUser(user: UserAccount) {
        const {username, token, authorities, avatar} = user;
        if (username) {
            localStorage.setItem('username', username);
        }
        if (token) {
            localStorage.setItem('token', token);
        }
        if (authorities) {
            localStorage.setItem('authorities', JSON.stringify(authorities));
        }
        if (avatar) {
            localStorage.setItem('avatar', avatar);
        }
    }

    return {login, captcha, initUser, clearUser};
}
