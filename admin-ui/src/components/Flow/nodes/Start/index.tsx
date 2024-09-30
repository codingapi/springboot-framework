import {HtmlNode, HtmlNodeModel, Model} from '@logicflow/core';
import React from "react";
import ReactDOM from "react-dom/client";
import {ProForm} from "@ant-design/pro-components";
import "./index.scss";
import {PlayCircleFilled, SettingFilled} from "@ant-design/icons";

type StartProperties = {
    name: string;
    update?: (values: any) => void;
    settingVisible?: boolean;
}

export const StartView: React.FC<StartProperties> = (props) => {
    const [visible, setVisible] = React.useState(false);
    const [form] = ProForm.useForm();

    return (
        <div className="start-node">
            <PlayCircleFilled
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

class StartModel extends HtmlNodeModel {
    setAttributes() {
        this.width = 200;
        this.height = 40;
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
    type: 'start-node',
    view: StartNode,
    model: StartModel,
};

