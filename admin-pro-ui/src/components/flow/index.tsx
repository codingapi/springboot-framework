import React, {useEffect, useRef} from "react";
import '@logicflow/core/es/index.css';
import '@logicflow/extension/lib/style/index.css';
import {LogicFlow} from "@logicflow/core";
import {DndPanel, Menu, MiniMap, Snapshot} from "@logicflow/extension";
import Start from "@/components/flow/nodes/Start";
import Node from "@/components/flow/nodes/Node";
import Over from "@/components/flow/nodes/Over";
import Circulate from "@/components/flow/nodes/Circulate";
import ControlPanel from "@/components/flow/panel/ControlPanel";
import NodePanel from "@/components/flow/panel/NodePanel";
import {EdgeType} from "@/components/flow/flow/types";

import "./index.scss";
import FlowPanelContext from "@/components/flow/domain/FlowPanelContext";

export interface FlowActionType {
    getData: () => any;
}

interface FlowProps {
    data?: LogicFlow.GraphConfigData;
    actionRef?: React.Ref<any>;
    edgeType?: EdgeType;
}

interface FlowContextProps {
    flowPanelContext: FlowPanelContext;
}

export const FlowContext = React.createContext<FlowContextProps | null>(null);

const Flow: React.FC<FlowProps> = (props) => {

    // 流程图背景颜色
    const FLOW_BACKGROUND_COLOR = '#f3f5f8';
    // 流程图的边颜色
    const FLOW_EDGE_COLOR = '#8f94e3';
    // 流程图的边宽度
    const FLOW_EDGE_STROKE_WIDTH = 1;

    const container = useRef<HTMLDivElement>(null);
    const lfRef = useRef<LogicFlow>(null);
    const edgeType = props.edgeType || 'polyline';

    const flowPanelContext = new FlowPanelContext(lfRef);

    if (props.actionRef) {
        React.useImperativeHandle(props.actionRef, () => ({
            getData: () => {
                return lfRef.current?.getGraphData();
            }
        }), [props]);
    }

    const data = props?.data || {};
    useEffect(() => {
        const SilentConfig = {
            isSilentMode: false,
            stopScrollGraph: true,
            stopMoveGraph: true,
            stopZoomGraph: true,
            edgeTextEdit: false,
        };

        //@ts-ignore
        lfRef.current = new LogicFlow({
            //@ts-ignore
            container: container.current,
            ...SilentConfig,
            background: {
                backgroundColor: FLOW_BACKGROUND_COLOR
            },
            plugins: [Menu, DndPanel, MiniMap, Snapshot],
            grid: false,
            keyboard: {
                enabled: true,
                shortcuts: [
                    {
                        keys: ['ctrl + v', 'cmd + v'],
                        callback: (e) => {
                            flowPanelContext.copyNode();
                        }
                    },

                ]
            },
            edgeType: edgeType,
        });

        lfRef.current.setTheme({
            bezier: {
                stroke: FLOW_EDGE_COLOR,
                strokeWidth: FLOW_EDGE_STROKE_WIDTH,
            },
            polyline: {
                stroke: FLOW_EDGE_COLOR,
                strokeWidth: FLOW_EDGE_STROKE_WIDTH,
            },
            line: {
                stroke: FLOW_EDGE_COLOR,
                strokeWidth: FLOW_EDGE_STROKE_WIDTH,
            },
        });
        lfRef.current.register(Start);
        lfRef.current.register(Node);
        lfRef.current.register(Over);
        lfRef.current.register(Circulate);

        lfRef.current.render(data);

        lfRef.current.on('node:add', (data) => {
            console.log('node:add', data);
        });

    }, []);



    //@ts-ignore
    window.lfRef = lfRef;


    return (
        <FlowContext.Provider value={{
            flowPanelContext: flowPanelContext
        }}>
            <div className="flow-design">
                <NodePanel/>
                <ControlPanel/>

                <div className={"flow-view"} ref={container}/>
            </div>
        </FlowContext.Provider>
    )
};

export default Flow;
