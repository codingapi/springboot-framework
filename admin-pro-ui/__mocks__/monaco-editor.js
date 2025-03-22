module.exports = {
    editor: {
        create: jest.fn(() => ({
            dispose: jest.fn(),
            getValue: jest.fn(() => ''),
            setValue: jest.fn(),
        })),
    },
};
