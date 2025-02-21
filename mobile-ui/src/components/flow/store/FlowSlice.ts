import {configureStore, createSlice, PayloadAction} from '@reduxjs/toolkit';

export interface FlowStore {
    // 流程记录ID
    recordId:string;
    // 请求加载中
    requestLoading: boolean;
}

export type FlowStoreAction = {
    updateState: (state: FlowStore, action: PayloadAction<any>) => void;
}

export const flowSlice = createSlice<FlowStore, FlowStoreAction, "flow", {}>({
    name: 'flow',
    initialState: {
        recordId:'',
        requestLoading:false
    },
    reducers: {
        updateState: (state, action) => {
            const currentState = action.payload;
            const keys = Object.keys(currentState);
            if(keys.includes('requestLoading')) {
                state.requestLoading = currentState.requestLoading;
            }
            if(keys.includes('recordId')) {
                state.recordId = action.payload.recordId;
            }
        },
    },
});


export const {
    updateState
} = flowSlice.actions;
export const flowStore = configureStore({
    reducer: {
        flow: flowSlice.reducer
    },
});

export type FlowReduxState = ReturnType<typeof flowStore.getState>;

