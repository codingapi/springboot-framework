import {configureStore, createSlice, PayloadAction} from '@reduxjs/toolkit';

export interface FlowStore {

}

export type FlowStoreAction = {

}

export const flowSlice = createSlice<FlowStore, FlowStoreAction, "flow", {}>({
    name: 'flow',
    initialState: {

    },
    reducers: {

    },
});


export const {

} = flowSlice.actions;
export const flowStore = configureStore({
    reducer: {
        flow: flowSlice.reducer
    },
});

export type FlowReduxState = ReturnType<typeof flowStore.getState>;

