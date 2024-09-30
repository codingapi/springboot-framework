import React, {useEffect, useRef} from "react";
import '@logicflow/core/es/index.css';
import '@logicflow/extension/lib/style/index.css';
import "./index.scss";

import {LogicFlow} from "@logicflow/core";
import {DndPanel, Menu, MiniMap, Snapshot} from "@logicflow/extension";
import Start from "@/components/Flow/nodes/Start";
import ControlPanel from "@/components/Flow/layout/ControlPanel";
import NodePanel from "@/components/Flow/layout/NodePanel";


const Flow = () => {
    const container = useRef<HTMLDivElement | null>(null);
    const lfRef = useRef<LogicFlow | null>(null);
    const [mapVisible, setMapVisible] = React.useState(false);

    const data = {
        nodes: [
            {
                id: '1',
                type: 'start-node',
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
            background: {
                backgroundColor: '#f3f5f8'
            },
            plugins: [Menu, DndPanel, MiniMap, Snapshot],
            grid: false,
        });

        lfRef.current.setTheme({
            bezier: {
                stroke: '#afafaf',
                strokeWidth: 1,
            },
        });
        lfRef.current.register(Start);
        lfRef.current.render(data);
    }, []);

    return (
        <div className="flow-content">
            <NodePanel className={"flow-panel"}/>
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
