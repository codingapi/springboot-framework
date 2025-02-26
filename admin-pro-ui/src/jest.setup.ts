import '@testing-library/jest-dom';
import {TextEncoder} from 'util';

// 添加全局对象
global.TextEncoder = TextEncoder;

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: jest.fn().mockImplementation(query => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(),
        removeListener: jest.fn(),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn(),
    })),
});

// Mock ResizeObserver
global.ResizeObserver = class ResizeObserver {
    observe() {
    }

    unobserve() {
    }

    disconnect() {
    }
};

// Suppress findDOMNode warnings in test output
const originalError = console.error;
console.error = (...args) => {
    if (/Warning.*findDOMNode/.test(args[0])) {
        return;
    }
    originalError.call(console, ...args);
};

// Mock getComputedStyle
global.getComputedStyle = jest.fn().mockImplementation(element => ({
    getPropertyValue: jest.fn().mockReturnValue(''),
    // Add any other style properties your tests might need
}));
