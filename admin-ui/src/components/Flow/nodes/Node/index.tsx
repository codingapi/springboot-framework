import {HtmlNode, HtmlNodeModel} from '@logicflow/core';
import React from "react";
import ReactDOM from "react-dom/client";
import "./index.scss";
import {PlusCircleFilled, SettingFilled} from "@ant-design/icons";
import NodeSettingPanel from "@/components/Flow/panel/node";

type NodeProperties = {
    name: string;
    code: string;
}

interface NodeProps {
    name: string;
    update?: (values: any) => void;
    settingVisible?: boolean;
    properties?: NodeProperties;
}

export const NodeView: React.FC<NodeProps> = (props) => {
    const [visible, setVisible] = React.useState(false);

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

            <NodeSettingPanel
                visible={visible}
                setVisible={setVisible}
                properties={props.properties}
                onSettingChange={(values) => {
                    props.update && props.update(values);
                }}
            />
        </div>
    );
}

class NodeModel extends HtmlNodeModel {
    setAttributes() {
        this.width = 200;
        this.height = 45;
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
                properties={properties}
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

