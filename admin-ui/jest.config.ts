import type { Config } from 'jest';

const config: Config = {
    preset: 'ts-jest',
    testEnvironment: 'jsdom',
    transform: {
        '^.+\\.(ts|tsx)$': ['ts-jest', {
            useESM: true,
        }],
        '^.+\\.(js|jsx|mjs)$': ['babel-jest', {
            presets: [
                ['@babel/preset-env', {
                    targets: {
                        node: 'current',
                    },
                }],
                '@babel/preset-react',
                '@babel/preset-typescript'
            ],
        }],
    },
    moduleNameMapper: {
        '^monaco-editor$': '<rootDir>/__mocks__/monaco-editor.js',
        "@logicflow": "<rootDir>/node_modules/@logicflow/core/dist/index.min.js",
        '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
        '\\.(jpg|jpeg|png|gif|webp|svg)$': '<rootDir>/__mocks__/fileMock.js',
        '^@/(.*)$': '<rootDir>/src/$1'
    },
    setupFilesAfterEnv: ['<rootDir>/src/jest.setup.ts'],
    testMatch: [
        "**/__test__/**/*.[jt]s?(x)",
        "**/__tests__/**/*.[jt]s?(x)",
        "**/?(*.)+(spec|test).[jt]s?(x)"
    ],
    moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
    transformIgnorePatterns: [
        'node_modules/(?!(lodash-es|@ant-design|@logicflow|other-esm-modules)/)'
    ],
};

export default config;
