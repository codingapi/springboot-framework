import Mock from "mockjs";
import type {IncomingMessage, ServerResponse} from "node:http";
import {NextFunction} from "@rsbuild/core/dist-types/types/config";

export const productsHandler = async (req: IncomingMessage, res: ServerResponse, next: NextFunction) => {
    const url = req.url;
    const method = req.method?.toUpperCase();
    if(url?.startsWith("/api/products") && method === 'GET') {
        const products = Mock.mock({
            'list|100': [{
                'id|+1': 1,
                'name': '@name',
                'price|100-1000': 1,
            }]
        }).list;
        res.setHeader('Content-Type', 'application/json');
        res.end(JSON.stringify({
            success: true,
            data: {
                list: products,
                total: products.length
            },
        }), 'utf-8');
        return;
    }

    next();
}
