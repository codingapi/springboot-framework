import {get, post} from "@/api";

export async function captcha() {
    return get('/open/captcha');
}

export async function login(body: any) {
    return post('/user/login', body);
}

export function initUser(user: {
    username: string;
    token: string;
    authorities: string[];
    avatar: string;
    data: any;
}) {
    const { username, token, authorities, avatar, data } = user;
    localStorage.setItem('username', username);
    localStorage.setItem('token', token);
}


export function clearUser() {
    localStorage.removeItem('username');
    localStorage.removeItem('token');
}
