import {get} from "@/api/index";


export async function products() {
    return get('/api/products');
}

