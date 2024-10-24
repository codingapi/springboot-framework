import React from "react";
import "./NodePanel.scss";
import {StartView} from "@/components/Flow/nodes/Start";
import {NodeView} from "@/components/Flow/nodes/Node";
import {OverView} from "@/components/Flow/nodes/Over";
import GroovyScript from "@/components/Flow/utils/script";

interface NodePanelProps {
    className: string,
    onDrag: (type: string, properties: any) => void;
}

const NodePanel: React.FC<NodePanelProps> = (props) => {

    return (
        <div className={props.className}>
            <h3 className={"panel-title"}>流程节点</h3>
            <div className={"node-content"}>
                <div
                    className={"node-item"}
                    onMouseDown={() => {
                        props.onDrag('start-node',
                            {
                                name: '开始节点',
                                code: 'start',
                                type: 'START',
                                view: 'default',
                                operatorMatcher: GroovyScript.anyOperatorMatcher,
                                editable: true,
                                titleGenerator: GroovyScript.defaultTitleGenerator,
                                errTrigger: '',
                                approvalType: 'UN_SIGN',
                                timeout: 0
                            }
                        );
                    }}
                >
                    <StartView name={"开始节点"}/>
                </div>

                <div
                    className={"node-item"}
                    onMouseDown={() => {
                        props.onDrag('node-node',
                            {
                                name: '流程节点',
                                code: 'flow',
                                type: 'APPROVAL',
                                view: 'default',
                                operatorMatcher: GroovyScript.anyOperatorMatcher,
                                editable: true,
                                titleGenerator: GroovyScript.defaultTitleGenerator,
                                errTrigger: '',
                                approvalType: 'SIGN',
                                timeout: 0
                            }
                        );
                    }}
                >
                    <NodeView name={"流程节点"}/>
                </div>

                <div
                    className={"node-item"}
                    onMouseDown={() => {
                        props.onDrag('over-node',
                            {
                                name: '结束节点',
                                code: 'over',
                                type: 'OVER',
                                view: 'default',
                                operatorMatcher: GroovyScript.creatorOperatorMatcher,
                                editable: true,
                                titleGenerator: GroovyScript.defaultTitleGenerator,
                                errTrigger: '',
                                approvalType: 'UN_SIGN',
                                timeout: 0
                            }
                        );
                    }}
                >
                    <OverView name={"结束节点"}/>
                </div>
            </div>
        </div>
    )
}

export default NodePanel;
