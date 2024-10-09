import {HtmlNode, HtmlNodeModel} from '@logicflow/core';
import React from "react";
import ReactDOM from "react-dom/client";
import "./index.scss";
import {CheckCircleFilled, SettingFilled} from "@ant-design/icons";
import OverSettingPanel from "@/components/Flow/panel/over";

type OverProperties = {
    name: string;
    code: string;
}
interface OverProps{
    name: string;
    update?: (values: any) => void;
    settingVisible?: boolean;
    properties?: OverProperties;
}

export const OverView: React.FC<OverProps> = (props) => {
    const [visible, setVisible] = React.useState(false);

    return (
        <div className="over-node">
            <CheckCircleFilled
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

            <OverSettingPanel
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

class OverModel extends HtmlNodeModel {
    setAttributes() {
        this.width = 200;
        this.height = 45;
        this.text.editable = false;
        this.menu = [];

        this.sourceRules = [
            {
                message: `不允许输出`,
                validate: (sourceNode, targetNode:any, sourceAnchor, targetAnchor) => {
                    const edges = this.graphModel.getNodeIncomingEdge(targetNode.id);
                    if (edges.length >= 0) {
                        return false;
                    } else {
                        return true;
                    }
                },
            },
        ];

        this.anchorsOffset = [
            // [this.width / 2, 0],
            // [0, this.height / 2],
            // [-this.width / 2, 0],
            [0, -this.height / 2],
        ];
    }


}

class OverNode extends HtmlNode {
    setHtml(rootEl: SVGForeignObjectElement) {
        const {properties} = this.props.model as HtmlNodeModel<OverProperties>;
        const div = document.createElement('div');
        ReactDOM.createRoot(div).render(
            <OverView
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

export default  {
    type: 'over-node',
    view: OverNode,
    model: OverModel,
};

