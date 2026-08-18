import {configureStore} from '@reduxjs/toolkit';
import {counterSlice} from './CounterSlice';
import {menuSlice} from './MenuSlice';

const store = configureStore({
    reducer: {
        counter: counterSlice.reducer,
        menu: menuSlice.reducer,
    },
});

export type RootState = ReturnType<typeof store.getState>;
export default store;