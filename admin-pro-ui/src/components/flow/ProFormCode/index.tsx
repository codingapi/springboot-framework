import React from 'react';
import {ProForm, ProFormItemProps} from "@ant-design/pro-components";
import CodeEditor, {CodeEditorProps} from "@/components/flow/CodeEditor";

interface ProFormCodeProps extends ProFormItemProps {
    codeEditorProps?: CodeEditorProps;
}

const ProFormCode: React.FC<ProFormCodeProps> = (props) => {

    const itemProps = {
        ...props,
        codeEditorProps: undefined
    };

    return (
        <ProForm.Item
            {...itemProps}
        >
            <CodeEditor {...props.codeEditorProps}/>
        </ProForm.Item>

    );
};

export default ProFormCode;
