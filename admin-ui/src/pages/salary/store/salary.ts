import {configureStore, createSlice, PayloadAction} from '@reduxjs/toolkit';
import {Table1, Table2, User} from "@/pages/salary/types";

export interface SalaryStore {
    users: User[];
    table1: Table1[];
    table2: Table2[];
    table2Version:number;
}

export type SalaryStoreAction = {

    updateUsers: (state: SalaryStore, action: PayloadAction<User[]>) => void;

    updateTable1: (state: SalaryStore, action: PayloadAction<Table1[]>) => void;

    updateTable2: (state: SalaryStore, action: PayloadAction<Table2[]>) => void;

    updateTable2Version: (state: SalaryStore, action: PayloadAction<number>) => void;
}

export const SalarySlice = createSlice<SalaryStore, SalaryStoreAction, "salary", {}>({
    name: 'salary',
    initialState: {
        users: [],
        table1: [],
        table2: [],
        table2Version:0
    },
    reducers: {

        updateUsers: (state, action) => {
            state.users = action.payload;
        },

        updateTable1: (state, action) => {
            state.table1 = action.payload;
        },

        updateTable2: (state, action) => {
            state.table2 = action.payload;
        },

        updateTable2Version: (state, action) => {
            state.table2Version = action.payload;
        }

    },
});

export const {
    updateUsers,
    updateTable1,
    updateTable2,
    updateTable2Version
} = SalarySlice.actions;

export const salaryStore = configureStore({
    reducer: {
        salary: SalarySlice.reducer
    },
});

export type SalaryState = ReturnType<typeof salaryStore.getState>;
