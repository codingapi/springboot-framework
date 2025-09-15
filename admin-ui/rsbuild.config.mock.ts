import {defineConfig} from '@rsbuild/core';
import commonConfig from './rsbuild.config';
import {usersHandler} from "./mocks/user";
import {productsHandler} from "./mocks/product";

export default defineConfig({
    ...commonConfig,
    server: {
        port: 8000,
    },
    dev:{
        setupMiddlewares:(middlewares, devServer) => {
            if (!devServer) {
                throw new Error('webpack-dev-server is not defined');
            }
            console.log('mock server is running');
            middlewares.unshift(usersHandler,productsHandler);
        }
    }
})

