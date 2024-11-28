import {HtmlNode, HtmlNodeModel} from '@logicflow/core';
import React from "react";
import ReactDOM from "react-dom/client";
import "./index.scss";
import {InboxOutlined, SettingFilled} from "@ant-design/icons";
import CirculateSettingPanel from "@/components/Flow/panel/circulate";
import {NodeState} from "@/components/Flow/nodes/states";
import {Tag} from "antd";
import StateLabel from "@/components/Flow/nodes/StateLabel";

type CirculateProperties = {
    id: string;
    name: string;
    code: string;
    type: string;
    view: string;
    operatorMatcher: string;
    editable: boolean;
    titleGenerator: string;
    errTrigger: string;
    approvalType: string;
    timeout: number;
    settingVisible?: boolean;
    state?: NodeState;
}

interface CirculateProps {
    name: string;
    code?: string;
    update?: (values: any) => void;
    settingVisible?: boolean;
    properties?: CirculateProperties;
}

export const CirculateView: React.FC<CirculateProps> = (props) => {
    const [visible, setVisible] = React.useState(false);

    const state = props.properties?.state;

    return (
        <div className="circulate-node">
            <InboxOutlined
                className={"icon"}
            />
            <div>
                <span className={"code"}>
                    {props.code && (
                        <> ({props.code})</>
                    )}
                </span>
                <span className={"title"}>{props.name}</span>
            </div>

            {props.settingVisible && (
                <SettingFilled
                    className={"setting"}
                    onClick={() => {
                        setVisible(true);
                    }}
                />
            )}

            {state && (
                <div className={"state"}>
                    <StateLabel
                        state={state}/>
                </div>
            )}

            <CirculateSettingPanel
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

class CirculateModel extends HtmlNodeModel {
    setAttributes() {
        this.width = 250;
        this.height = 45;
        this.text.editable = false;
        this.menu = [];

        this.sourceRules = [
            {
                message: `不允许输出`,
                validate: (sourceNode: any, targetNode: any, sourceAnchor, targetAnchor) => {
                    const edges = this.graphModel.getNodeOutgoingEdge(sourceNode.id);
                    if (edges.length >= 1) {
                        return false;
                    } else {
                        return true;
                    }
                },
            },
        ];

        this.anchorsOffset = [
            [this.width / 2, 0],
            [0, this.height / 2],
            [-this.width / 2, 0],
            [0, -this.height / 2],
        ];
    }


}

class CirculateNode extends HtmlNode {
    setHtml(rootEl: SVGForeignObjectElement) {
        const {properties} = this.props.model as HtmlNodeModel<CirculateProperties>;
        const div = document.createElement('div');
        const settingVisible = properties.settingVisible !== false;
        ReactDOM.createRoot(div).render(
            <CirculateView
                name={properties.name}
                code={properties.code}
                properties={properties}
                settingVisible={settingVisible}
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
    type: 'circulate-node',
    view: CirculateNode,
    model: CirculateModel,
};

