import React, {useEffect, useRef} from "react";
import '@logicflow/core/es/index.css';
import '@logicflow/extension/lib/style/index.css';
import "./index.scss";

import {LogicFlow} from "@logicflow/core";
import {DndPanel, Menu, MiniMap, Snapshot} from "@logicflow/extension";
import Start from "@/components/Flow/nodes/Start";
import Node from "@/components/Flow/nodes/Node";
import Over from "@/components/Flow/nodes/Over";
import ControlPanel from "@/components/Flow/layout/ControlPanel";
import NodePanel from "@/components/Flow/layout/NodePanel";
import {message} from "antd";
import {copy} from "@/components/Flow/panel/shortcuts";

export interface FlowActionType {
    getData:()=>any;
}

interface FlowProps {
    data?: LogicFlow.GraphConfigData;
    actionRef?:React.Ref<any>
}

const Flow: React.FC<FlowProps> = (props) => {
    const container = useRef<HTMLDivElement>(null);
    const lfRef = useRef<LogicFlow>(null);
    const [mapVisible, setMapVisible] = React.useState(false);

    if(props.actionRef){
        React.useImperativeHandle(props.actionRef, () => ({
            getData: () => {
                return lfRef.current?.getGraphData();
            }
        }),[props]);
    }

    const data = props?.data || {};
    useEffect(() => {
        const SilentConfig = {
            isSilentMode: false,
            stopScrollGraph: true,
            stopMoveGraph: true,
            stopZoomGraph: true,
        };

        //@ts-ignore
        lfRef.current = new LogicFlow({
            //@ts-ignore
            container: container.current,
            ...SilentConfig,
            background: {
                backgroundColor: '#f3f5f8'
            },
            plugins: [Menu, DndPanel, MiniMap, Snapshot],
            grid: false,
            keyboard:{
                enabled: true,
                shortcuts: [
                    {
                        keys: ['ctrl + v', 'cmd + v'],
                        callback: () => {
                            // @ts-ignore
                            return copy(lfRef.current);
                        }
                    }
                ]
            },
            edgeType: 'bezier',
        });

        lfRef.current.setTheme({
            bezier: {
                stroke: '#afafaf',
                strokeWidth: 1,
            },
        });
        lfRef.current.register(Start);
        lfRef.current.register(Node);
        lfRef.current.register(Over);

        lfRef.current.render(data);

        lfRef.current.on('node:add', (data) => {
            console.log('node:add', data);
        });

    }, []);


    const nodeVerify = async (type: string) => {
        //@ts-ignore
        const nodes = lfRef.current?.getGraphData().nodes;
        if (type === 'start-node') {
            for (let i = 0; i < nodes.length; i++) {
                if (nodes[i].type === type) {
                    message.error('开始节点只能有一个');
                    return false;
                }
            }
        }
        if (type === 'over-node') {
            for (let i = 0; i < nodes.length; i++) {
                if (nodes[i].type === type) {
                    message.error('结束节点只能有一个');
                    return false;
                }
            }
        }
        return true;
    }

    return (
        <div className="flow-content">
            <NodePanel
                className={"flow-panel"}
                onDrag={async (type, properties) => {
                    if (await nodeVerify(type)) {
                        lfRef.current?.dnd.startDrag({
                            type: type,
                            properties: properties
                        });
                    }
                }}
            />
            <ControlPanel
                className={"flow-control"}
                onZoomIn={() => {
                    lfRef.current?.zoom(true);
                }}
                onZoomOut={() => {
                    lfRef.current?.zoom(false);
                }}
                onZoomReset={() => {
                    lfRef.current?.resetZoom();
                    lfRef.current?.resetTranslate();
                }}
                onRedo={() => {
                    lfRef.current?.redo();
                }}
                onUndo={() => {
                    lfRef.current?.undo();
                }}
                onMiniMap={() => {
                    if (mapVisible) {
                        //@ts-ignore
                        lfRef.current?.extension.miniMap.hide();
                    } else {
                        const modelWidth = lfRef.current?.graphModel.width;
                        //@ts-ignore
                        lfRef.current?.extension.miniMap.show(modelWidth - 300, 200);
                    }
                    setMapVisible(!mapVisible);
                }}
                onDownload={() => {
                    lfRef.current?.getSnapshot();
                }}

            />
            <div className={"flow-view"} ref={container}/>
        </div>
    )
};

export default Flow;
