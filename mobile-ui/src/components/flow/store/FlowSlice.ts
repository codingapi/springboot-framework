import {configureStore, createSlice, PayloadAction} from '@reduxjs/toolkit';
import {FlowResultMessage} from "@/components/flow/types";

export interface FlowStore {
    // 流程记录ID
    recordId: string;
    // 请求加载中
    requestLoading: boolean;
    // 流程结果
    result: FlowResultMessage | null;
    // 隐藏page的内容
    contentHiddenVisible: boolean;

    // 意见框展示状态
    opinionVisible: boolean;

    // 延期时间窗口状态
    postponedVisible: boolean;

}

export type FlowStoreAction = {
    updateState: (state: FlowStore, action: PayloadAction<any>) => void;
    initState: (state: FlowStore) => void;
}

export const flowSlice = createSlice<FlowStore, FlowStoreAction, "flow", {}>({
    name: 'flow',
    initialState: {
        recordId: '',
        requestLoading: false,
        result: null,
        contentHiddenVisible: false,
        postponedVisible: false,
        opinionVisible:true,
    },
    reducers: {
        updateState: (state, action) => {
            const currentState = action.payload;
            const keys = Object.keys(currentState);
            if (keys.includes('requestLoading')) {
                state.requestLoading = currentState.requestLoading;
            }
            if (keys.includes('recordId')) {
                state.recordId = action.payload.recordId;
            }
            if (keys.includes('result')) {
                state.result = action.payload.result;
                state.contentHiddenVisible =  state.result!=null;
            }
            if (keys.includes('postponedVisible')) {
                state.postponedVisible = action.payload.postponedVisible;
            }
        },
        initState: (state) => {
            state.recordId = '';
            state.requestLoading = false;
            state.result = null;
            state.postponedVisible = false;
            state.opinionVisible = true;
            state.contentHiddenVisible = false;
        }
    },
});


export const {
    updateState,
    initState
} = flowSlice.actions;
export const flowStore = configureStore({
    reducer: {
        flow: flowSlice.reducer
    },
});

export type FlowReduxState = ReturnType<typeof flowStore.getState>;

