import React, {Ref, useEffect, useImperativeHandle} from 'react';
import * as monaco from 'monaco-editor';

export interface ProFormCodeActionType {
  resetValue: (value: string) => void;
  getSelectedValue: () => string;
  getValue:()=>string;
}

interface ProFormCodeProps {
  language?: string,
  value?: string,
  onChange?: (value: string) => void,
  onSelectedRun?: (value: string) => void;
  style?: React.CSSProperties;
  readonly?:boolean;
  actionRef?: Ref<ProFormCodeActionType|undefined>;
}

const ProFormCode: React.FC<ProFormCodeProps> = (props) => {
  const language = props.language || 'sql';

  const actionRef = props.actionRef;

  if(actionRef) {
    useImperativeHandle(actionRef, () => ({
      resetValue: (value: string) => {
        const editors = monaco.editor.getEditors();
        if(editors.length>0){
          const editor = editors[0];
          editor.setValue(value);
        }
      },
      getSelectedValue: () => {
        const editors = monaco.editor.getEditors();
        if (editors.length > 0) {
          const editor = editors[0];
          const selection = editor.getSelection();
          //@ts-ignore
          return editor.getModel().getValueInRange(selection);
        }
        return "";
      },
      getValue:()=>{
        const editors = monaco.editor.getEditors();
        if (editors.length > 0) {
          const editor = editors[0];
          return editor.getValue();
        }
        return "";
      }
    }), []);
  }

  useEffect(() => {
    const model = monaco.editor.createModel(props.value || "", language);
    const editor = monaco.editor.create(
      //@ts-ignore
      document.getElementById('editorRef'),
      {
        automaticLayout: true,
        model: model,
        fontSize: 14,
        theme: "vs-dark",
        readOnly: props.readonly,
      },
    );

    const subscription = editor.onDidChangeModelContent((event) => {
      props.onChange && props.onChange(editor.getValue());
    });

    let runSelectedCodeActionDispose: monaco.IDisposable | null = null;
    if(props.onSelectedRun) {
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

      if(runSelectedCodeActionDispose!==null){
        runSelectedCodeActionDispose.dispose();
      }
    };
  }, [props.readonly]);

  return (
    <div id={"editorRef"} style={props.style}></div>
  );
};

export default ProFormCode;
