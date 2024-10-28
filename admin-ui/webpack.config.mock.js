const {merge} = require('webpack-merge');
const common = require('./webpack.common.js');
const webpackMockServer = require('webpack-mock-server');
const mockFunction = require('./mocks/index.js');
const express = require('express');

module.exports = merge(common, {
    mode: 'development',
    devServer: {
        port: 3000,

        setupMiddlewares: (middlewares, devServer) => {
            if (!devServer) {
                throw new Error('webpack-dev-server is not defined');
            }
            // 使用 express.json() 来解析请求体
            devServer.app.use(express.json());

            // 使用 webpackMockServer 来添加 mock 功能
            webpackMockServer.use(devServer.app);
            webpackMockServer.add(mockFunction)(devServer.app);

            console.log('mock server is running');
            return middlewares; // 返回 middlewares
        }
    },
});
