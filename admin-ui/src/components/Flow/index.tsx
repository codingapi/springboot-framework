import React, {useEffect, useRef} from "react";
import '@logicflow/core/es/index.css';
import '@logicflow/extension/lib/style/index.css';
import "./index.scss";

import {LogicFlow} from "@logicflow/core";
import boxx from "@/components/Flow/nodes/Start";
import Control from "@/components/Flow/layout/Control";
import NodePanel from "@/components/Flow/layout/NodePanel";


const Flow = () => {
    const container = useRef<HTMLDivElement | null>(null);
    const lfRef = useRef<LogicFlow | null>(null);

    const data = {
        nodes: [
            {
                id: '11',
                type: 'boxx',
                x: 350,
                y: 100,
                properties: {
                    name: 'turbo',
                    body: 'hello',
                },
            },
        ],
        edges: [],
    }


    useEffect(() => {
        const SilentConfig = {
            stopScrollGraph: true,
            stopMoveGraph: true,
            stopZoomGraph: true,
        };

        lfRef.current = new LogicFlow({
            //@ts-ignore
            container: container.current,
            ...SilentConfig,
            grid:false,
            height: 800,
        });


        lfRef.current.setTheme({
            bezier: {
                stroke: '#afafaf',
                strokeWidth: 1,
            },
        });
        lfRef.current.register(boxx);
        lfRef.current.render(data);
    }, []);

    return (
        <div className="flow-content">
            <NodePanel className={"flow-panel"} />
            <Control className={"flow-control"}/>
            <div className={"flow-view"} ref={container}/>
        </div>
    )
};

export default Flow;
