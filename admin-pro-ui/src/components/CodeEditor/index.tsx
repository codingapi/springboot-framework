import React, {Ref, useEffect, useImperativeHandle} from 'react';
import * as monaco from 'monaco-editor';

export interface CodeEditorAction {
    // 重置编辑器的值
    resetValue: (value: string) => void;
    // 获取选中的值
    getSelectedValue: () => string;
    // 获取编辑器的值
    getValue: () => string;
    // 获取编辑器实例
    getEditor:() => monaco.editor.IStandaloneCodeEditor | null;
}


interface CodeEditorProps {
    language?: string,
    value?: string,
    onChange?: (value: string) => void,
    onSelectedRun?: (value: string) => void;
    style?: React.CSSProperties;
    readonly?: boolean;
    theme?: string;
    fontSize?: number;
    actionRef?: Ref<CodeEditorAction>;
}


const CodeEditor: React.FC<CodeEditorProps> = (props) => {
    const language = props.language || 'javascript';
    const theme = props.theme || "vs-dark";
    const fontSize = props.fontSize || 14;
    const [editorId, setEditorId] = React.useState<string>("");

    const container = React.useRef(null);

    const style = props.style || {
        height: "80px",
    }

    const codeEditorAction = {
        getEditor: ()=>{
            const editors =  monaco.editor.getEditors();
            if(editors.length > 0) {
                return editors.find(editor => editor.getId() === editorId);
            }
            return null;
        },

        resetValue: (value: string) => {
            const editor = codeEditorAction.getEditor();
            if(editor) {
                const position = editor.getPosition();
                editor.setValue(value);
                if (position) {
                    editor.setPosition({
                        lineNumber: position.lineNumber,
                        column: position.column
                    });
                }
            }
        },
        getSelectedValue: () => {
            const editor = codeEditorAction.getEditor();
            if(editor) {
                const selection = editor.getSelection();
                //@ts-ignore
                return editor.getModel().getValueInRange(selection);
            }
            return "";
        },
        getValue: () => {
            const editor = codeEditorAction.getEditor();
            if(editor) {
                return editor.getValue();
            }
            return "";
        }
    } as CodeEditorAction;

    useImperativeHandle(props.actionRef, () => (codeEditorAction), [props.actionRef]);

    useEffect(() => {
        if(props.value) {
            codeEditorAction.resetValue(props.value);
        }
    }, [props.value]);

    useEffect(() => {
        const model = monaco.editor.createModel(props.value || "", language);
        const editor = monaco.editor.create(
            //@ts-ignore
            container?.current,
            {
                automaticLayout: true,
                model: model,
                fontSize: fontSize,
                theme: theme,
                readOnly: props.readonly,
            },
        );

        setEditorId(editor.getId());

        const subscription = editor.onDidChangeModelContent((event) => {
            props.onChange && props.onChange(editor.getValue());
        });

        let runSelectedCodeActionDispose: monaco.IDisposable | null = null;
        if (props.onSelectedRun) {
            const runSelectedCodeAction = {
                id: "run-code",
                label: "Run Selected Code",
                contextMenuGroupId: "navigation",
                keybindings: [
                    monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyR,
                    monaco.KeyMod.WinCtrl | monaco.KeyCode.KeyR,
                ],
                run: () => {
                    const selection = editor.getSelection();
                    //@ts-ignore
                    const selectedText = editor.getModel().getValueInRange(selection);
                    props.onSelectedRun && props.onSelectedRun(selectedText);
                },
            }
            runSelectedCodeActionDispose = monaco.editor.addEditorAction(runSelectedCodeAction);
        }

        return () => {
            editor.dispose();
            subscription.dispose();
            model.dispose();

            if (runSelectedCodeActionDispose !== null) {
                runSelectedCodeActionDispose.dispose();
            }
        };
    }, [props.readonly]);

    return (
        <div ref={container} style={style}></div>
    );
};

export default CodeEditor;
