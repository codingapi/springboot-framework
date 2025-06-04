const {merge} = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common,{
    mode: 'production',
    devServer: {
        port: 8000,
    },
});
