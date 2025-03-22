# React Unit Test for Jest

1. dependencies on devDependencies  
```
@testing-library/dom  
@testing-library/jest-dom
@testing-library/react
@types/jest
jest
ts-jest
jest-environment-jsdom
identity-obj-proxy
ts-node
```
2. add jest.config.ts and add jest script in package.json

```
//rootDir/jest.config.ts
import type { Config } from 'jest';

const config: Config = {
    preset: 'ts-jest',
    testEnvironment: 'jsdom',
    transform: {
        '^.+\\.(ts|tsx)$': 'ts-jest',
        '^.+\\.(js|jsx)$': 'babel-jest',
    },
    moduleNameMapper: {
        '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
        '\\.(jpg|jpeg|png|gif|webp|svg)$': '<rootDir>/__mocks__/fileMock.js',
        '^@/(.*)$': '<rootDir>/src/$1'
    },
    setupFilesAfterEnv: ['<rootDir>/src/jest.setup.ts'],
    testRegex: '(/__tests__/.*|(\\.|/)(test|spec))\\.(jsx?|tsx?)$',
    moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node']
};

export default config;


```

on jest.config.ts will setting up the jest configuration for the project.
some important configuration is moduleNameMapper, this setting configuration for style,asset,and project path alias.
setupFilesAfterEnv is the file that will be executed before the test run, in this case jest.setup.ts
testRegex is regex for the test file, in this case the test file should be end with .test.tsx or .spec.tsx


on `package.json` add jest script
```
"scripts":{
    "test": "jest",
    "test:watch": "jest --watch"
    ... other scripts
}
```

3. add jest.setup.ts and fileMock.js in src folder
```
// rootDir/src/jest.setup.ts
import '@testing-library/jest-dom';
```

```

// rootDir/__mocks__/fileMock.js
module.exports = 'test-file-stub';

```

4. create test file in src folder, example my component file in `rootDir/src/pages/welcome/index.tsx` then the test file should be in `rootDir/src/pages/welcome/__tests__/index.test.tsx`
```

import React from 'react';
import Welcome from '../index';
import {render} from "@testing-library/react";
import store from "@/store/Redux";
import {Provider} from "react-redux";

// test description for the component
describe('Welcome Component', () => {
    // test case for the component
    test('renders with default initial value', () => {
        // render the component
        render(
            <Provider store={store}>
                <Welcome />
            </Provider>
        );
        // get the element that will be tested
        const countElement = document.querySelector('.App-header p');
        // assert the element is exist
        expect(countElement).toBeInTheDocument();
        // assert the element text content
        expect(countElement).toHaveTextContent('hi , Redux counter: 0, Roles:');
        // log the element text content
        console.log(countElement?.textContent);
    });
});

```

5. test router component

> specify the router path in the test file
```
import { render, fireEvent } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import UserProfile from '../UserProfile';

describe('UserProfile Component', () => {
  test('renders user profile with correct ID', () => {
  
    //  use memory router and setting initial router path
    const { getByTestId } = render(
      <MemoryRouter initialEntries={['/user/123']}>
        <Routes>
          <Route path="/user/:id" element={<UserProfile />} />
        </Routes>
      </MemoryRouter>
    );

    expect(getByTestId('user-id')).toHaveTextContent('User ID: 123');
  });
});
```
> or use `BrowserRouter` and `render` function
```
import { render, fireEvent } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';
import App from '../App';

describe('App Navigation', () => {
  test('full app navigation', () => {
    const history = createMemoryHistory();
    const { getByText } = render(
      <Router location={history.location} navigator={history}>
        <App />
      </Router>
    );
    fireEvent.click(getByText('Go to Profile'));
    expect(history.location.pathname).toBe('/profile');
  });
});
```

6. test axios api calling
```

import { render, waitFor } from '@testing-library/react';
import axios from 'axios';
import { UserProfile } from '../UserProfile';

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('UserProfile Component', () => {
  // test case for the component
  test('loads all user data correctly - URL based', async () => {
    // mock the axios get function
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

    const { getByTestId } = render(<UserProfile userId="123" />);

    await waitFor(() => {
      expect(getByTestId('user-info')).toHaveTextContent('John Doe');
      expect(getByTestId('user-posts')).toHaveTextContent('Post 1');
      expect(getByTestId('user-followers')).toHaveTextContent('Follower 1');
    });
  });
  
```
