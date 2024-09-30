import {HtmlNode, HtmlNodeModel} from '@logicflow/core';
import React from "react";
import ReactDOM from "react-dom/client";
import {ProForm} from "@ant-design/pro-components";
import "./index.scss";
import {CheckCircleFilled, SettingFilled} from "@ant-design/icons";
import SettingPanel from "@/components/Flow/nodes/SettingPanel";

type OverProperties = {
    name: string;
    update?: (values: any) => void;
    settingVisible?: boolean;
}

export const OverView: React.FC<OverProperties> = (props) => {
    const [visible, setVisible] = React.useState(false);
    const [form] = ProForm.useForm();

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

            <SettingPanel
                visible={visible}
                setVisible={setVisible}
                properties={props}
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
        this.height = 40;
        this.text.editable = false;
        this.menu = [];

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

