import {post} from "@/api/index";

export async function login(body: Account.LoginRequest) {
    return post('/user/login', body);
}

export function clearUser() {
    localStorage.removeItem('username');
    localStorage.removeItem('token');
    localStorage.removeItem('authorities');
    localStorage.removeItem('avatar');
}

export function initUser(user: {
    username: string;
    token: string;
    authorities: string[];
    avatar: string;
}) {
    const {username, token, authorities, avatar} = user;
    localStorage.setItem('username', username);
    localStorage.setItem('token', token);
    if(authorities) {
        localStorage.setItem('authorities', JSON.stringify(authorities));
    }
    localStorage.setItem('avatar', avatar || "/logo.png");
}