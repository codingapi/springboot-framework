import {httpClient} from "@/api/index";


export async function products() {
    return httpClient.get('/api/products');
}

