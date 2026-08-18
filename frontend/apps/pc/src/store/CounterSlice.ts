import {createSlice} from '@reduxjs/toolkit';

export interface CounterStore {
    value: number;
}

export type CounterStoreAction = {
    increment: (state: CounterStore) => void;
    decrement: (state: CounterStore) => void;
    clear: (state: CounterStore) => void;
}

export const counterSlice = createSlice<CounterStore, CounterStoreAction, "counter", {}>({
    name: 'counter',
    initialState: {
        value: 0,
    },
    reducers: {
        increment: (state) => {
            state.value += 1;
        },
        decrement: (state) => {
            state.value -= 1;
        },
        clear: (state) => {
            state.value = 0;
        },
    },
});

export const {increment, decrement, clear} = counterSlice.actions;

