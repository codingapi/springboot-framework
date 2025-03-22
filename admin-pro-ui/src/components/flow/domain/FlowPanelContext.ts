import React from "react";
import {LogicFlow} from "@logicflow/core";
import {CustomButtonType, NodeButtonProperties, NodeProperties, NodeType} from "@/components/flow/types";
import {message} from "antd";
import {isEmpty} from "lodash-es";
import NodeData = LogicFlow.NodeData;
import RegisterConfig = LogicFlow.RegisterConfig;
import GraphConfigData = LogicFlow.GraphConfigData;
import GroovyScript from "@/components/flow/utils/script";

// 节点移动距离
const TRANSLATION_DISTANCE = 40

// 逻辑面板上下文
class FlowPanelContext {

    private readonly lfRef: React.RefObject<LogicFlow>;

    // 按钮事件选项
    private readonly buttonEventOptions = [
        {
            label: "保存",
            value: "SAVE"
        },
        {
            label: "发起",
            value: "START"
        },
        {
            label: "提交",
            value: "SUBMIT"
        },
        {
            label: "预提交",
            value: "TRY_SUBMIT"
        },
        {
            label: "指定人员提交",
            value: "SPECIFY_SUBMIT"
        },
        {
            label: "驳回",
            value: "REJECT"
        },
        {
            label: "转办",
            value: "TRANSFER"
        },
        {
            label: "撤销",
            value: "RECALL"
        },
        {
            label: "延期",
            value: "POSTPONED"
        },
        {
            label: "催办",
            value: "URGE"
        },
        {
            label: "自定义接口",
            value: "CUSTOM"
        },
        {
            label: "自定义事件",
            value: "VIEW"
        },
        {
            label: "删除",
            value: "REMOVE"
        },
    ] as {
        label: string;
        value: CustomButtonType;
    }[];

    constructor(lfRef: React.RefObject<LogicFlow>) {
        this.lfRef = lfRef;
    }

    /**
     * 获取按钮事件选项
     * @param value 事件值
     */
    convertButtonValue = (value: string) => {
        return this.buttonEventOptions.find(item => item.value === value)?.label;
    }

    /**
     * 获取按钮事件选项
     */
    getButtonEventOptions() {
        return this.buttonEventOptions;
    }

    /**
     * 生成uuid
     */
    private generateUUID() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            const r = (Math.random() * 16) | 0;
            const v = c === 'x' ? r : (r & 0x3) | 0x8;
            return v.toString(16);
        });
    }

    /**
     * 注册节点
     * @param node
     */
    register(node: RegisterConfig) {
        this.lfRef.current?.register(node);
    }

    /**
     * 渲染数据
     * @param data
     */
    render(data: GraphConfigData) {
        this.lfRef.current?.render(data);
    }

    /**
     * 获取节点信息
     * @param nodeId 节点id
     */
    getNode(nodeId: string) {
        const data = this.getGraphData();
        if (data) {
            //@ts-ignore
            const nodes = data.nodes;
            const getNode = (nodeId: string) => {
                for (const node of nodes) {
                    if (node.id === nodeId) {
                        return node;
                    }
                }
            }
            return getNode(nodeId);
        }
        return null;
    }

    /**
     * 获取节点按钮
     * @param nodeId 节点id
     */
    getButtons(nodeId: string) {
        const node = this.getNode(nodeId);
        if (node) {
            const buttons = node.properties.buttons || [];
            buttons.sort((a: any, b: any) => {
                return a.order - b.order;
            })
            return buttons;
        }
        return []
    }


    /**
     * 删除节点按钮
     * @param nodeId 节点id
     * @param buttonId 按钮id
     */
    deleteButton(nodeId:string,buttonId:string){
        const data = this.getGraphData();
        if(data) {
            //@ts-ignore
            const nodes = data.nodes;
            const getNode = (nodeId: String) => {
                for (const node of nodes) {
                    if (node.id === nodeId) {
                        return node;
                    }
                }
            }
            const node = getNode(nodeId);
            const buttons = node.properties.buttons || [];
            node.properties.buttons = buttons.filter((item: any) => item.id !== buttonId);
            this.render(data as GraphConfigData);
        }
    }

    /**
     * 更新节点按钮数据
     */
    updateButton(nodeId: string, button: NodeButtonProperties) {
        const data = this.getGraphData();
        if (data) {
            //@ts-ignore
            const nodes = data.nodes;
            const getNode = (nodeId: String) => {
                for (const node of nodes) {
                    if (node.id === nodeId) {
                        return node;
                    }
                }
            }
            const node = getNode(nodeId);
            const buttons = node.properties.buttons || [];
            let update = false;
            for (const item of buttons) {
                if (item.id == button.id) {
                    item.name = button.name;
                    item.style = button.style;
                    item.type = button.type;
                    item.order = button.order;
                    item.groovy = button.groovy;
                    item.eventKey = button.eventKey;
                    update = true;
                }
            }
            if (!update) {
                button.id = this.generateUUID();
                node.properties.buttons = [...buttons, button];
            }
            this.render(data as GraphConfigData);
        }
    }


    /**
     * 添加节点
     * @param type 节点类型
     * @param properties 节点属性
     */
    addNode(type: NodeType, properties: NodeProperties) {
        if (this.nodeVerify(type)) {
            const uid = this.generateUUID();
            this.lfRef.current?.dnd.startDrag({
                id: uid,
                type: type,
                properties: {
                    ...properties,
                    id: uid
                }
            })
        }
    }


    /**
     * 获取节点下的边
     * @param nodeId 节点id
     */
    getEdges(nodeId: String) {
        const data = this.getGraphData() as any;
        const list = []
        if(data) {
            const nodes = data.nodes;
            const getNodeProperties = (nodeId: String) => {
                for (const node of nodes) {
                    if (node.id === nodeId) {
                        return node.properties;
                    }
                }
            }

            let update = false;

            let order = 0;
            if (data && data.edges) {
                const edges = data.edges;
                for (const index in edges) {
                    const edge = edges[index];
                    if (edge.sourceNodeId === nodeId) {
                        order++;
                        if (!edge.properties.outTrigger) {
                            edge.properties = {
                                ...edge.properties,
                                outTrigger: GroovyScript.defaultOutTrigger,
                                order: order,
                                back: false
                            }
                            update = true;
                        }

                        list.push({
                            id: edge.id,
                            name: edge.properties.name,
                            source: getNodeProperties(edge.sourceNodeId),
                            target: getNodeProperties(edge.targetNodeId),
                            outTrigger: edge.properties.outTrigger,
                            back: edge.properties.back,
                            order: edge.properties.order,
                            edge
                        });
                    }
                }
            }

            if (update) {
                this.render(data);
            }
        }

        return list;
    }


    /**
     * 修改边的名称
     * @param edgeId 边id
     * @param name 名称
     */
    changeEdgeName(edgeId:string,name:string){
        const data = this.getGraphData() as any;
        if (data && data.edges) {
            const edges = data.edges;
            for (const index in edges) {
                const edge = edges[index];
                if (edge.id === edgeId) {
                    edge.properties.name = name;
                    this.render(data);
                }
            }
        }
    }


    /**
     * 修改边的排序
     * @param edgeId 边id
     * @param order 排序
     */
    changeEdgeOrder(edgeId:string,order:number){
        const data = this.getGraphData() as any;
        if (data && data.edges) {
            const edges = data.edges;
            for (const index in edges) {
                const edge = edges[index];
                if (edge.id === edgeId) {
                    edge.properties.order = order;
                    this.render(data);
                }
            }
        }
    }

    /**
     * 修改边的退回属性
     * @param edgeId 边id
     * @param back 是否退回
     */
    changeEdgeBack(edgeId:string,back:boolean){
        const data = this.getGraphData() as any;
        if (data && data.edges) {
            const edges = data.edges;
            for (const index in edges) {
                const edge = edges[index];
                if (edge.id === edgeId) {
                    edge.properties.back = back;
                    this.render(data);
                }
            }
        }
    }


    /**
     * 修改边的触发器
     * @param edgeId 边id
     * @param outTrigger 触发器
     */
    changeEdgeOutTrigger(edgeId:string,outTrigger:string){
        const data = this.getGraphData() as any;
        if (data && data.edges) {
            const edges = data.edges;
            for (const index in edges) {
                const edge = edges[index];
                if (edge.id === edgeId) {
                    edge.properties.outTrigger = outTrigger;
                    this.render(data);
                }
            }
        }
    }


    /**
     * 复制节点 控制位置的偏移
     * @param nodeData
     * @param distance
     * @private
     */
    private translateNodeData(nodeData: NodeData, distance: number) {
        nodeData.x += distance
        nodeData.y += distance

        if (!isEmpty(nodeData.text)) {
            nodeData.text.x += distance
            nodeData.text.y += distance
        }

        return nodeData
    }


    /**
     * 从粘贴板中复制节点
     */
    copyNode = () => {
        const flow = this.lfRef.current;
        if (!flow) {
            return;
        }
        const selected = flow.getSelectElements(true);
        if (selected && (selected.nodes || selected.edges)) {
            flow.clearSelectElements();
            if (selected.nodes) {
                const nodes = selected.nodes;
                for (const node of nodes) {
                    if (node.type === 'start-node') {
                        message.error('开始节点只能有一个').then();
                        return false;
                    }
                    if (node.type === 'over-node') {
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
                    this.translateNodeData(node, TRANSLATION_DISTANCE);
                });
            }
        }
        return false;
    }


    /**
     * 节点校验
     * @param type
     */
    private nodeVerify = (type: NodeType) => {
        // @ts-ignore
        const nodes = this.getGraphData().nodes;
        if (type === 'start-node') {
            for (let i = 0; i < nodes.length; i++) {
                if (nodes[i].type === type) {
                    message.error('开始节点只能有一个').then();
                    return false;
                }
            }
        }
        if (type === 'over-node') {
            for (let i = 0; i < nodes.length; i++) {
                if (nodes[i].type === type) {
                    message.error('结束节点只能有一个').then();
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 缩放
     * @param flag true为放大 false为缩小
     */
    zoom = (flag: boolean) => {
        this.lfRef.current?.zoom(flag);
    }

    /**
     * 重置缩放
     */
    resetZoom = () => {
        this.lfRef.current?.resetZoom();
        this.lfRef.current?.resetTranslate();
    }

    /**
     * 恢复 下一步
     */
    redo = () => {
        this.lfRef.current?.redo();
    }

    /**
     * 撤销 上一步
     */
    undo = () => {
        this.lfRef.current?.undo();
    }

    /**
     * 隐藏地图
     */
    hiddenMap = () => {
        // @ts-ignore
        this.lfRef.current?.extension.miniMap.hide();
    }

    /**
     * 显示地图
     */
    showMap = () => {
        const modelWidth = this.lfRef.current?.graphModel.width;
        // @ts-ignore
        this.lfRef.current?.extension.miniMap.show(modelWidth - 300, 200);
    }

    /**
     * 下载图片
     */
    download = () => {
        this.lfRef.current?.getSnapshot();
    }

    /**
     * 获取流程设计的数据
     */
    getGraphData() {
        return this.lfRef.current?.getGraphData();
    }
}

export default FlowPanelContext;
