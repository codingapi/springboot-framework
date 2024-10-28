const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");
const {dependencies} = require("./package.json");

module.exports = {
    entry: './src/index.tsx',
    ignoreWarnings: [
        {
            module: /@babel\/standalone/, // 忽略来自 @babel/standalone 的警告
            message: /Critical dependency: the request of a dependency is an expression/,
        },
    ],
    devServer: {
        port: 3000,
    },
    resolve: {
        extensions: ['.ts', '.tsx', '.js'],
        alias: {
            '@': path.resolve(__dirname, 'src'),
        },
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/,
            },
            {
                test: /\.(css|s[ac]ss)$/,  // 匹配 .css, .scss, .sass 文件
                use: [
                    'style-loader',  // 将 CSS 插入到 DOM 中
                    'css-loader',    // 解析 CSS
                    'sass-loader',   // 解析 Sass 文件（对于 .scss 和 .sass 文件）
                ],
            },
            {
                test: /\.(png|jpg|gif|svg)$/,
                type: 'asset/resource',
            },
            // 使用 'asset/inline' 将图片转为 JS 模块
            // {
            //     test: /\.(png|jpg|gif|svg)$/,
            //     type: 'asset/inline',  // 使用 'asset/inline' 将图片转为 JS 模块
            //     parser: {
            //         dataUrlCondition: {
            //             maxSize: 100 * 1024,  // 超过100KB的图片将不会转为内联
            //         },
            //     },
            // },
        ],
    },
    plugins: [
        new CleanWebpackPlugin(),
        new MonacoWebpackPlugin(),
        new HtmlWebpackPlugin({
            template: './public/index.html',
        }),
        new CopyWebpackPlugin({
            patterns: [
                {
                    from: 'public',
                    to: '.',
                    globOptions: {
                        ignore: ['**/index.html'],
                    },
                }
            ],
        }),
        new ModuleFederationPlugin({
            name: dependencies["name"],

            shared: {
                // some other dependencies
                react: { // react
                    singleton: true,
                    requiredVersion: dependencies["react"],
                    eager: true,
                },
                "react-dom": { // react-dom
                    singleton: true,
                    requiredVersion: dependencies["react-dom"],
                    eager: true,
                },
            },
        }),
    ],
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, 'dist'),
    },
};
