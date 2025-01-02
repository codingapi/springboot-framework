import React from 'react';
import Welcome from '../index';
import {render} from "@testing-library/react";
import store from "@/store/Redux";
import {Provider} from "react-redux";
import {Calculate} from "../data";

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


    test('method add test', () => {
        const sum = Calculate.add(1,1);
        expect(sum).toBe(2);
    });
});
