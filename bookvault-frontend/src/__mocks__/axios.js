const mock = {
  create: () => ({
    get: jest.fn(async () => ({ data: {} })),
    post: jest.fn(async () => ({ data: {} })),
    put: jest.fn(async () => ({ data: {} })),
    delete: jest.fn(async () => ({ data: {} })),
    interceptors: { request: { use: jest.fn() } }
  })
}

export default mock

