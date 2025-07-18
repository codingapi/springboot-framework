import * as path from 'path';
import {defineConfig} from '@rsbuild/core';
import {pluginReact} from '@rsbuild/plugin-react';
import {pluginSass} from '@rsbuild/plugin-sass';
import {pluginModuleFederation} from '@module-federation/rsbuild-plugin';

export default defineConfig({
    plugins: [
        pluginReact(),
        pluginSass(),
        pluginModuleFederation({
            name: "MobileUI",
            shared: {
                react: {
                    requiredVersion: '^18.3.1',
                    singleton: true,
                    strictVersion: false,
                },
                'react-dom': {
                    requiredVersion: '^18.3.1',
                    singleton: true,
                    strictVersion: false,
                },
            }
        }, {
            ssr: false,
            ssrDir: path.resolve(__dirname, 'ssr'),
            environment: 'development',
        }),
    ],
    server:{
        port: 8000,
    },
    source: {
        entry: {
            index: './src/entry.tsx',
        },
        decorators: {
            version: 'legacy',
        },
    },
    resolve:{
        alias:{
            '@': path.resolve(__dirname, 'src'),
            react: path.resolve(__dirname, 'node_modules/react'),
            'react-dom': path.resolve(__dirname, 'node_modules/react-dom'),
        }
    },
    html: {
        template: './public/index.html',
    },
    performance: {
        chunkSplit: {
            strategy: 'split-by-size',
            minSize: 10000,
            maxSize: 30000,
        },
    },
    tools: {
    }
});
