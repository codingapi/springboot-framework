import {HtmlNode, HtmlNodeModel} from '@logicflow/core';
import React from "react";
import ReactDOM from "react-dom/client";
import {ProForm} from "@ant-design/pro-components";
import "./index.scss";
import {PlusCircleFilled, SettingFilled} from "@ant-design/icons";

type NodeProperties = {
    name: string;
    update?: (values: any) => void;
    settingVisible?: boolean;
}

export const NodeView: React.FC<NodeProperties> = (props) => {
    const [visible, setVisible] = React.useState(false);
    const [form] = ProForm.useForm();

    return (
        <div className="node-node">
            <PlusCircleFilled
                className={"icon"}
            />
            <span
                className={"title"}
            >{props.name}</span>

            {props.settingVisible && (
                <SettingFilled
                    className={"setting"}
                    onClick={() => {
                        setVisible(true);
                    }}
                />
            )}
        </div>
    );
}

class NodeModel extends HtmlNodeModel {
    setAttributes() {
        this.width = 200;
        this.height = 40;
        this.text.editable = false;
        this.menu = [];

        this.anchorsOffset = [
            [this.width / 2, 0],
            [0, this.height / 2],
            [-this.width / 2, 0],
            [0, -this.height / 2],
        ];
    }


}

class NodeNode extends HtmlNode {
    setHtml(rootEl: SVGForeignObjectElement) {
        const {properties} = this.props.model as HtmlNodeModel<NodeProperties>;
        const div = document.createElement('div');
        ReactDOM.createRoot(div).render(
            <NodeView
                name={properties.name}
                settingVisible={true}
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

export default {
    type: 'node-node',
    view: NodeNode,
    model: NodeModel,
};

