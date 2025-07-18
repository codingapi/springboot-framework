import type {IncomingMessage} from "node:http";

export const parseBody = (req: IncomingMessage): Promise<any> => {
    return new Promise((resolve, reject) => {
        let body = '';
        req.on('data', (chunk) => {
            body += chunk.toString('utf-8');
        });
        req.on('end', () => {
            try {
                resolve(JSON.parse(body));
            } catch (e) {
                resolve({});
            }
        });
        req.on('error', (err) => reject(err));
    });
};

export const getQuery = (req: IncomingMessage) => {
    const reqUrl = new URL(req.url || '', 'http://localhost');
    return Object.fromEntries(reqUrl.searchParams.entries());
}
