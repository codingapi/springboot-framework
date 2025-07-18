import {defineConfig} from '@rsbuild/core';
import commonConfig from './rsbuild.config';

export default defineConfig({
    ...commonConfig,
    server: {
        port: 8000,
        proxy: {
            '/api': 'http://127.0.0.1:8090',
            '/open': 'http://127.0.0.1:8090',
            '/user': 'http://127.0.0.1:8090',
        },
    },
})

