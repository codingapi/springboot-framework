import React from 'react';
import {render} from "@testing-library/react";
import axios from 'axios';
import FlowPage from "@/pages/flow/work";

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// test description for the component
describe('Welcome Component', () => {
    test('renders with default initial value', () => {

        mockedAxios.get.mockImplementation((url) => {
            if (url === '/api/users/123') {
                return Promise.resolve({
                    data: { name: 'John Doe', email: 'john@example.com' }
                });
            }
            if (url === '/api/users/123/posts') {
                return Promise.resolve({
                    data: [{ id: 1, title: 'Post 1' }]
                });
            }
            if (url === '/api/users/123/followers') {
                return Promise.resolve({
                    data: [{ id: 1, name: 'Follower 1' }]
                });
            }
            return Promise.reject(new Error('Not found'));
        });

        // render the component
        const {getByTestId} =render(
          <FlowPage/>
        );

        const flowTable = getByTestId("flow-table");
        expect(flowTable).toBeInTheDocument();
    });
});
