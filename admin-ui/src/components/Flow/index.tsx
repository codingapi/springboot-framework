import React, {useEffect, useRef} from "react";
import '@logicflow/core/es/index.css';
import '@logicflow/extension/lib/style/index.css';
import {LogicFlow} from "@logicflow/core";
import boxx from "@/components/Flow/nodes/Start";
import NodeData = LogicFlow.NodeData;

const Flow = () => {
    const container = useRef<HTMLDivElement | null>(null);

    const nodes = [
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
    ] as NodeData[]

    const data = {
        nodes: nodes,
        edges: [],
    }


    useEffect(() => {

        const SilentConfig = {
            stopScrollGraph: true,
            stopMoveGraph: true,
            stopZoomGraph: true,
        };

        const lf = new LogicFlow({
            //@ts-ignore
            container: container.current,
            ...SilentConfig,
            grid:false,
            height: 800,
        });


        lf.setTheme({
            bezier: {
                stroke: '#afafaf',
                strokeWidth: 1,
            },
        });
        lf.register(boxx)
        lf.render(data)

    }, []);

    return (
        <>
            <div ref={container}/>
        </>
    )
};

export default Flow;
