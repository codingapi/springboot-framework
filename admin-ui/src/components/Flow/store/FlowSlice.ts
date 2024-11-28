import {configureStore, createSlice, PayloadAction} from '@reduxjs/toolkit';
import {FlowResultMessage, FlowUser, UserSelectMode} from "@/components/Flow/flow/types";

export interface FlowStore {
    // 延期时间窗口
    postponedVisible: boolean;
    // 延期时间
    timeOut: number;

    // 流程结果
    result: FlowResultMessage | null;
    // 流程结果展示
    resultVisible: boolean;
    // 连带关闭流程
    resultCloseFlow: boolean;

    // 选人类型
    userSelectType: string;
    // 选人窗口
    userSelectVisible: boolean;
    // 选人模式
    userSelectMode: UserSelectMode;
    // 选人数据
    currentUsers: FlowUser[];
    // 指定人员
    specifyUserIds: number[];
}

export type FlowStoreAction = {
    showPostponed: (state: FlowStore) => void;
    setTimeOut: (state: FlowStore, action: PayloadAction<number>) => void;
    clearPostponed: (state: FlowStore) => void;

    showResult: (state: FlowStore, action: PayloadAction<{
        closeFlow: boolean,
        result: FlowResultMessage
    }>) => void;
    clearResult: (state: FlowStore) => void;

    setUserSelectModal: (state: FlowStore, action: PayloadAction<{
        mode: UserSelectMode,
        visible: boolean,
        type: string,
        specifyUserIds: number[]
    }>) => void;
    setSelectUsers: (state: FlowStore, action: PayloadAction<FlowUser[]>) => void;
    closeUserSelect: (state: FlowStore) => void;
    clearUserSelect: (state: FlowStore) => void;
}

export const flowSlice = createSlice<FlowStore, FlowStoreAction, "flow", {}>({
    name: 'flow',
    initialState: {
        postponedVisible: false,
        timeOut: 0,
        result: null,
        resultVisible: false,
        userSelectVisible: false,
        userSelectMode: 'single',
        userSelectType: '',
        resultCloseFlow: false,
        currentUsers: [],
        specifyUserIds: [],
    },
    reducers: {
        showPostponed: (state) => {
            state.postponedVisible = true;
        },
        setTimeOut: (state, action) => {
            state.timeOut = action.payload;
        },
        clearPostponed: (state) => {
            state.postponedVisible = false;
            state.timeOut = 0;
        },

        showResult: (state, action) => {
            state.result = action.payload.result;
            state.resultCloseFlow = action.payload.closeFlow;
            state.resultVisible = true;
        },

        clearResult: (state) => {
            state.result = null;
            state.resultVisible = false;
            state.resultCloseFlow = false;
        },

        setUserSelectModal: (state, action) => {
            state.userSelectMode = action.payload.mode;
            state.userSelectVisible = action.payload.visible;
            state.userSelectType = action.payload.type;
            state.specifyUserIds = action.payload.specifyUserIds;
        },

        setSelectUsers: (state, action) => {
            state.userSelectVisible = false;
            state.currentUsers = action.payload;
        },

        closeUserSelect: (state) => {
            state.userSelectVisible = false;
        },

        clearUserSelect: (state) => {
            state.userSelectVisible = false;
            state.userSelectMode = 'single';
            state.currentUsers = [];
            state.specifyUserIds = [];
            state.userSelectType = '';
        }
    },
});


export const {
    showPostponed,
    setTimeOut,
    clearPostponed,
    showResult,
    clearResult,
    clearUserSelect,
    setUserSelectModal,
    closeUserSelect,
    setSelectUsers
} = flowSlice.actions;
export const flowStore = configureStore({
    reducer: {
        flow: flowSlice.reducer
    },
});

export type FlowReduxState = ReturnType<typeof flowStore.getState>;

