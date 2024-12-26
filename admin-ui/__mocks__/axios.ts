// __mocks__/axios.ts
const mockAxios = {
    create: jest.fn(() => ({
        interceptors: {
            request: {
                use: jest.fn(),
                eject: jest.fn()
            },
            response: {
                use: jest.fn(),
                eject: jest.fn()
            }
        },
        get: jest.fn(),
        post: jest.fn(),
        put: jest.fn(),
        delete: jest.fn(),
        patch: jest.fn()
    })),
    interceptors: {
        request: {
            use: jest.fn(),
            eject: jest.fn()
        },
        response: {
            use: jest.fn(),
            eject: jest.fn()
        }
    },
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
    patch: jest.fn()
};

export default mockAxios;
