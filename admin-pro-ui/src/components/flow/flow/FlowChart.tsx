import React, {useEffect, useRef} from "react";
import {FlowData} from "@/components/flow/flow/data";
import {LogicFlow} from "@logicflow/core";
import {DndPanel, Menu, MiniMap, Snapshot} from "@logicflow/extension";
import Start from "@/components/flow/nodes/Start";
import Node from "@/components/flow/nodes/Node";
import Over from "@/components/flow/nodes/Over";
import Circulate from "@/components/flow/nodes/Circulate";
import '@logicflow/core/es/index.css';
import '@logicflow/extension/lib/style/index.css';
import "./FlowChart.scss";
import {EdgeType} from "@/components/flow/flow/types";

interface FlowChartProps {
    flowData: FlowData;
    edgeType?: EdgeType;
}

const FlowChart: React.FC<FlowChartProps> = (props) => {

    const flowData = props.flowData;
    const edgeType = props.edgeType || 'polyline';
    const container = useRef<HTMLDivElement>(null);
    const lfRef = useRef<LogicFlow>(null);

    useEffect(() => {
        const SilentConfig = {
            isSilentMode: true,
            stopScrollGraph: false,
            stopMoveGraph: false,
            stopZoomGraph: false,
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

        lfRef.current.render(flowData.getFlowSchema());
    }, []);

    return (
        <>
            <div className="flow-chart-content">
                <div className={"flow-view"} ref={container}/>
            </div>
        </>
    )
}

export default FlowChart;
