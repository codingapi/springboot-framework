import {HtmlNode, HtmlNodeModel} from '@logicflow/core';
import React from "react";
import ReactDOM from "react-dom/client";
import "./index.scss";
import {PlayCircleFilled, SettingFilled} from "@ant-design/icons";
import StartSettingPanel from "@/components/Flow/panel/start";

type StartProperties ={
    name: string;
    code: string;
    view: string;
    outTrigger:string;
    outOperatorMatcher:string;
}

interface StartProps {
    name: string;
    code?: string;
    settingVisible?: boolean;
    update?: (values: any) => void;
    properties?: StartProperties;
}

export const StartView: React.FC<StartProps> = (props) => {
    const [visible, setVisible] = React.useState(false);

    return (
        <div className="start-node">
            <PlayCircleFilled
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

            <StartSettingPanel
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

class StartModel extends HtmlNodeModel {
    setAttributes() {
        this.width = 200;
        this.height = 45;
        this.text.editable = false;
        this.menu = [];

        this.anchorsOffset = [
            // [this.width / 2, 0],
            [0, this.height / 2],
            // [-this.width / 2, 0],
            // [0, -this.height / 2],
        ];
    }

}

class StartNode extends HtmlNode {
    setHtml(rootEl: SVGForeignObjectElement) {
        const {properties} = this.props.model as HtmlNodeModel<StartProperties>;
        const div = document.createElement('div');
        ReactDOM.createRoot(div).render(
            <StartView
                name={properties.name}
                code={properties.code}
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
    type: 'start-node',
    view: StartNode,
    model: StartModel,
};

