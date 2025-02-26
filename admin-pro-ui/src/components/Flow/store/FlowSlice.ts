import {configureStore, createSlice, PayloadAction} from '@reduxjs/toolkit';
import {FlowResultMessage, FlowUser, UserSelectMode, UserSelectType} from "@/components/Flow/flow/types";

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
    userSelectType: UserSelectType | null;
    // 选人窗口
    userSelectVisible: boolean;
    // 选人模式
    userSelectMode: UserSelectMode;
    // 选人数据
    currentUsers: FlowUser[];
    // 指定人员
    specifyUserIds: number[];

    // 自定义前端点击事件触发EventKey
    eventKey: string;

    // 设置FlowViewVisible
    flowViewVisible:boolean;

    // 审批意见输入框
    opinionEditorVisible: boolean;
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
        type: UserSelectType,
        specifyUserIds: number[]
    }>) => void;
    setSelectUsers: (state: FlowStore, action: PayloadAction<FlowUser[]>) => void;
    closeUserSelect: (state: FlowStore) => void;
    clearUserSelect: (state: FlowStore) => void;

    triggerEventClick: (state: FlowStore, action: PayloadAction<string>) => void;
    clearTriggerEventClick: (state: FlowStore) => void;

    showOpinionEditor: (state: FlowStore) => void;
    hideOpinionEditor: (state: FlowStore) => void;

    showFlowView: (state: FlowStore) => void;
    hideFlowView: (state: FlowStore) => void;
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
        userSelectType: null,
        resultCloseFlow: false,
        currentUsers: [],
        specifyUserIds: [],
        eventKey: '',
        opinionEditorVisible: true,
        flowViewVisible:false
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
            state.userSelectType = null;
        },

        triggerEventClick: (state, action) => {
            state.eventKey = action.payload;
        },

        clearTriggerEventClick: (state) => {
            state.eventKey = '';
        },

        showOpinionEditor: (state) => {
            state.opinionEditorVisible = true;
        },

        hideOpinionEditor: (state) => {
            state.opinionEditorVisible = false;
        },

        showFlowView:(state) => {
            state.flowViewVisible = true;
        },

        hideFlowView:(state) => {
            state.flowViewVisible = false;
        },
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
    setSelectUsers,
    triggerEventClick,
    clearTriggerEventClick,
    hideOpinionEditor,
    showOpinionEditor,
    showFlowView,
    hideFlowView
} = flowSlice.actions;
export const flowStore = configureStore({
    reducer: {
        flow: flowSlice.reducer
    },
});

export type FlowReduxState = ReturnType<typeof flowStore.getState>;

