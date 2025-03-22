import {createSlice} from '@reduxjs/toolkit';

export interface MenuStore {
    version: number;
}

export type MenuStoreAction = {
    refresh: (state: MenuStore) => void;
}

export const menuSlice = createSlice<MenuStore, MenuStoreAction, "menu", {}>({
    name: 'menu',
    initialState: {
        version: 0,
    },
    reducers: {
        refresh: (state) => {
            state.version = Math.random();
        },
    },
});

export const {refresh} = menuSlice.actions;

