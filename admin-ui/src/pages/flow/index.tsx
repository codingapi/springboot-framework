import React from "react";
import Flow from "@/components/Flow";

const FlowPage = () => {

    const data = {
        nodes: [
            {
                id: '1',
                type: 'start-node',
                x: 350,
                y: 100,
                properties: {
                    name: '开始节点',
                },
            },
        ],
        edges: [],
    }

    return (
        <Flow
            data={data}
            onSave={(data)=>{
                alert(data);
            }}
        />
    )
};

export default FlowPage;
