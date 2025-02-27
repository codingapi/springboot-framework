import React, {useEffect, useRef} from "react";
import '@logicflow/core/es/index.css';
import '@logicflow/extension/lib/style/index.css';
import {LogicFlow} from "@logicflow/core";
import {DndPanel, Menu, MiniMap, Snapshot} from "@logicflow/extension";
import Start from "@/components/flow/nodes/Start";
import Node from "@/components/flow/nodes/Node";
import Over from "@/components/flow/nodes/Over";
import Circulate from "@/components/flow/nodes/Circulate";
import ControlPanel from "@/components/flow/layout/ControlPanel";
import NodePanel from "@/components/flow/layout/NodePanel";
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
                backgroundColor: '#f3f5f8'
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
                stroke: '#8f94e3',
                strokeWidth: 1,
            },
            polyline: {
                stroke: '#8f94e3',
                strokeWidth: 1,
            },
            line: {
                stroke: '#8f94e3',
                strokeWidth: 1,
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
