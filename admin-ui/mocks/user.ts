import type {IncomingMessage, ServerResponse} from "node:http";
import {NextFunction} from "@rsbuild/core/dist-types/types/config";
import {parseBody} from "./index";

export const usersHandler = async (req: IncomingMessage, res: ServerResponse, next: NextFunction) => {
    const url = req.url;
    const method = req.method?.toUpperCase();
    if(url?.startsWith("/user/login") && method === 'POST') {
        const body = await parseBody(req);
        const username = body.username || '';
        res.setHeader('Content-Type', 'application/json');
        if(username==='admin'){
            res.end(JSON.stringify({
                success: true,
                data: {
                    'username': username,
                    'token': 'test token',
                    'avatar': '/logo.png',
                    'authorities': ['ROLE_ADMIN', 'ROLE_DEVELOPER'],
                }
            }), 'utf-8');
        }else {
            res.end(JSON.stringify({
                success: true,
                data: {
                    'username': username,
                    'token': 'test token',
                    'avatar': '/logo.png',
                    'authorities': ['ROLE_USER'],
                }
            }), 'utf-8');
        }
        return;
    }

    next();
}
