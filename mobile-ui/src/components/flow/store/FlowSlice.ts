import {configureStore, createSlice, PayloadAction} from '@reduxjs/toolkit';
import {FlowResultMessage, UserSelectFormType} from "@/components/flow/types";

export interface FlowStore {
    // 流程记录ID
    recordId: string;
    // 请求加载中
    requestLoading: boolean;
    // 流程结果
    result: FlowResultMessage | null;
    // 隐藏page的内容
    contentHiddenVisible: boolean;
    // 版本号
    version: number;
    // 数据版本号
    dataVersion: number;

    // 意见框展示状态
    opinionVisible: boolean;

    // 延期时间窗口状态
    postponedVisible: boolean;
    // 选人窗口状态
    userSelectVisible: boolean;
    // 选人状态
    userSelectMode: UserSelectMode | null;
}

export interface UserSelectMode {
    // 指定人员范围
    specifyUserIds?: number[];
    // 当前选择的人员
    currentUserIds?: number[];
    // 选人类型
    userSelectType: UserSelectFormType;
    // 是否多选
    multiple: boolean;
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
        opinionVisible: true,
        userSelectVisible: false,
        userSelectMode: null,
        version: 0,
        dataVersion: 0,
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
                state.contentHiddenVisible = state.result != null;
            }
            if (keys.includes('postponedVisible')) {
                state.postponedVisible = action.payload.postponedVisible;
            }

            if (keys.includes('userSelectMode')) {
                state.userSelectVisible = true;
                state.userSelectMode = action.payload.userSelectMode;
            }

            if (keys.includes('version')) {
                state.version = action.payload.version;
            }

            if (keys.includes('dataVersion')) {
                state.dataVersion = action.payload.dataVersion;
            }

            if (keys.includes('userSelectVisible')) {
                state.userSelectVisible = action.payload.userSelectVisible;
                if (!state.userSelectVisible) {
                    state.userSelectMode = null;
                }
            }
        },
        initState: (state) => {
            state.recordId = '';
            state.requestLoading = false;
            state.result = null;
            state.postponedVisible = false;
            state.opinionVisible = true;
            state.contentHiddenVisible = false;
            state.userSelectVisible = false;
            state.userSelectMode = null;
            state.version = 0;
            state.dataVersion = 0;
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

