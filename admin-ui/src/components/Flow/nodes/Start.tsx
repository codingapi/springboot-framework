import {HtmlNode, HtmlNodeModel} from '@logicflow/core';
import React from "react";
import ReactDOM from "react-dom/client";
import {DrawerForm, ProForm, ProFormText} from "@ant-design/pro-components";


type StartProperties = {
    name: string;
    body: string;
    update: (values: any) => void
}

const StartView: React.FC<StartProperties> = (props) => {
    const [visible, setVisible] = React.useState(false);
    const [form] = ProForm.useForm();

    return (
        <div>
            <h1 className="box-title">{props.name}</h1>
            <div className="box-content" onClick={() => {
                form.setFieldsValue(props);
                setVisible(true)
            }}>
                设置 -&gt; {props.body}
            </div>

            <DrawerForm
                open={visible}
                title={"title"}
                form={form}
                drawerProps={{
                    onClose: () => setVisible(false),
                    destroyOnClose: true,
                }}
                onFinish={async (values) => {
                    setVisible(false);
                    props.update(values);
                }}
            >

                <ProFormText
                    name="name"
                    label="name"
                />

                <ProFormText
                    name="body"
                    label="body"
                />

            </DrawerForm>
        </div>
    );
}

class StartModel extends HtmlNodeModel {
    setAttributes() {
        this.width = 140;
        this.height = 220;
        this.text.editable = false;
    }
}

class StartNode extends HtmlNode {
    setHtml(rootEl: SVGForeignObjectElement) {
        const {properties} = this.props.model as HtmlNodeModel<StartProperties>;
        const div = document.createElement('div');
        ReactDOM.createRoot(div).render(
            <StartView
                name={properties.name}
                body={properties.body}
                update={async (values) => {
                    this.props.model.setProperties(values);
                }}/>,
        );
        //需要清空
        rootEl.innerHTML = '';
        rootEl.appendChild(div);
        super.setHtml(rootEl);
    }
}

const Start = {
    type: 'start-node',
    view: StartNode,
    model: StartModel,
};

export default Start;
