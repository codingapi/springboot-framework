import GroovyScript from "@/components/Flow/utils/script";

const FlowUtils = {

    getEdges(nodeId: String) {
        //@ts-ignore
        const data = window.lfRef?.current.getGraphData();
        const list = []
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
                    order ++;
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

        if(update){
            this.render(data);
        }

        return list;
    },


    changeEdgeName(edgeId:string,name:string){
        //@ts-ignore
        const data = window.lfRef?.current.getGraphData();
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
    },


    changeEdgeOrder(edgeId:string,order:number){
        //@ts-ignore
        const data = window.lfRef?.current.getGraphData();
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
    },

    changeEdgeBack(edgeId:string,back:boolean){
        //@ts-ignore
        const data = window.lfRef?.current.getGraphData();
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
    },


    changeEdgeOutTrigger(edgeId:string,outTrigger:string){
        //@ts-ignore
        const data = window.lfRef?.current.getGraphData();
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
    },

    render(data: any) {
        //@ts-ignore
        window.lfRef?.current.render(data);
    }
}


export default FlowUtils;
