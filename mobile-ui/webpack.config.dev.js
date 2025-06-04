const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {
    mode: 'development',
    devServer: {
        port: 10000,
        proxy: [
            {
                context: ['/api', '/open', '/user'],
                target: 'http://localhost:8090',
                changeOrigin: true,
                logLevel: 'debug',
                onProxyReq: (proxyReq, req, res) => {
                    console.log('Proxying request:', req.url);
                },
            }
        ]
    }
});
