import {LogicFlow} from "@logicflow/core";
import {isEmpty} from 'lodash-es'
import NodeData = LogicFlow.NodeData;
import {message} from "antd";

function translateNodeData(nodeData: NodeData, distance: number) {
    nodeData.x += distance
    nodeData.y += distance

    if (!isEmpty(nodeData.text)) {
        nodeData.text.x += distance
        nodeData.text.y += distance
    }

    return nodeData
}

const TRANSLATION_DISTANCE = 40

export const copy = (flow: LogicFlow) => {
    const selected = flow.getSelectElements(true);
    if (selected && (selected.nodes || selected.edges)) {
        flow.clearSelectElements();

        if(selected.edges){
            // not support copy edges
            return false;
        }

        if(selected.nodes){
            const nodes = selected.nodes;
            for(const node of nodes){
                if(node.type ==='start-node'){
                    message.error('开始节点只能有一个').then();
                    return false;
                }
                if(node.type ==='over-node'){
                    message.error('结束节点只能有一个').then();
                    return false;
                }
            }
            const addElements = flow.addElements(
                selected,
                TRANSLATION_DISTANCE
            );
            if (!addElements) return true;
            addElements.nodes.forEach((node) => flow.selectElementById(node.id, true));
            addElements.nodes.forEach((node) => {
                translateNodeData(node, TRANSLATION_DISTANCE);
            });
        }

    }
    return false;
}
