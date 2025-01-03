import React from 'react';
import {act, fireEvent, render, screen, waitFor} from "@testing-library/react";
import FlowPage from "@/pages/flow/work";

jest.mock('axios');

// test description for the component
describe('Welcome Component', () => {

    test('renders with default initial value', async () => {

        // render the component
        render(
            <FlowPage/>
        );

        const flowTable = screen.getByTestId("flow-table");
        expect(flowTable).toBeInTheDocument();

        const flowAddBtn = screen.getByTestId("flow-add-btn");
        expect(flowAddBtn).toBeInTheDocument();


        await act(async () => {
            fireEvent.click(flowAddBtn);
        });

        await waitFor(() => {
            const flowEditor = screen.getByTestId("flow-editor");
            expect(flowEditor).toBeInTheDocument();
        });

        const inputTitle = screen.getByLabelText('标题');
        const inputCode = screen.getByLabelText('编码');
        const inputDescription = screen.getByLabelText('描述');
        const inputPostponedMax = screen.getByLabelText('最大延期次数');
        const inputSkipIfSameApprover = screen.getByLabelText('是否跳过相同审批人');
        const submitButton = screen.getByTestId('flow-editor-submit');
        expect(submitButton).toBeInTheDocument();


        await act(async () => {
            fireEvent.change(inputTitle, {target: {value: 'test'}});
            expect(inputTitle).toHaveValue('test');
            fireEvent.change(inputCode, {target: {value: 'test'}});
            expect(inputCode).toHaveValue('test');
            fireEvent.change(inputDescription, {target: {value: 'test'}});
            expect(inputDescription).toHaveValue('test');
            fireEvent.change(inputPostponedMax, {target: {value: '1'}});
            expect(inputPostponedMax).toHaveValue('1');
            fireEvent.change(inputSkipIfSameApprover, {target: {value: 'true'}});
            expect(inputSkipIfSameApprover).toHaveValue('true');
            fireEvent.click(submitButton);
        });

        await waitFor(() => {
            // todo 不敢展示，却必须得展示才能正常
            const submitButton = screen.getByTestId('flow-editor-submit');
            expect(submitButton).toBeInTheDocument();
        });

    });
});
