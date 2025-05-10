import '@testing-library/jest-dom';

Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: jest.fn().mockImplementation(query => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(), // 旧版 API
        removeListener: jest.fn(), // 旧版 API
        addEventListener: jest.fn(), // 新版 API
        removeEventListener: jest.fn(), // 新版 API
        dispatchEvent: jest.fn(),
    })),
});