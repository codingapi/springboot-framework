import {configureStore, createSlice, PayloadAction} from '@reduxjs/toolkit';

export interface LayoutStore {
    title: string;
    backHome:boolean;
}

export type MenuStoreAction = {
    changeTitle: (state: LayoutStore, action: PayloadAction<string>) => void;
}

export const layoutSlice = createSlice<LayoutStore, MenuStoreAction, "layout", {}>({
    name: 'layout',
    initialState: {
        title: '首页',
        backHome:true
    },
    reducers: {
        changeTitle: (state,action) => {
            const title = action.payload;
            state.title = title;
            state.backHome = '首页' === title;
        }
    },
});

export const {changeTitle} = layoutSlice.actions;

export const layoutStore = configureStore({
    reducer: {
        layout: layoutSlice.reducer
    },
});

export type LayoutState = ReturnType<typeof layoutStore.getState>;


