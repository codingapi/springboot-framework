const {merge} = require('webpack-merge');
const common = require('./webpack.common.js');
const webpackMockServer = require('webpack-mock-server');

module.exports = merge(common, {
    mode: 'development',
    devServer: {
        port: 8000,

        setupMiddlewares: (middlewares, devServer) => {
            if (!devServer) {
                throw new Error('webpack-dev-server is not defined');
            }

            // 使用 webpackMockServer 来添加 mock 功能
            webpackMockServer.use(devServer.app,{
                port: 8090,
                entry:[
                    './mocks/user.ts',
                    './mocks/product.ts',
                ],
                tsConfigFileName: "mocks/tsconfig.json"
            });

            console.log('mock server is running');
            return middlewares; // 返回 middlewares
        }
    },
});
