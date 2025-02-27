import GroovyScript from "@/components/flow/utils/script";

const FlowUtils = {

    generateUUID (){
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            const r = (Math.random() * 16) | 0;
            const v = c === 'x' ? r : (r & 0x3) | 0x8;
            return v.toString(16);
        });
    },

    getNode(nodeId:string){
        //@ts-ignore
        const data = window.lfRef?.current.getGraphData();
        const nodes = data.nodes;

        const getNode = (nodeId: String) => {
            for (const node of nodes) {
                if (node.id === nodeId) {
                    return node;
                }
            }
        }
        return getNode(nodeId);
    },

    getButtons(nodeId:string){
        const node = FlowUtils.getNode(nodeId);
        const buttons =  node.properties.buttons || [];
        buttons.sort((a:any,b:any)=>{
            return a.order - b.order;
        })
        return buttons;
    },


    updateButton(nodeId:string,button:any){
        //@ts-ignore
        const data = window.lfRef?.current.getGraphData();
        const nodes = data.nodes;
        const getNode = (nodeId: String) => {
            for (const node of nodes) {
                if (node.id === nodeId) {
                    return node;
                }
            }
        }
        const node = getNode(nodeId);
        const buttons =  node.properties.buttons || [];

        let update = false;

        for(const item of buttons){
            if(item.id == button.id){
                item.name = button.name;
                item.style = button.style;
                item.type = button.type;
                item.order = button.order;
                item.groovy = button.groovy;
                item.eventKey = button.eventKey;

                update = true;
            }
        }
        if(!update){
            button.id = FlowUtils.generateUUID();
            node.properties.buttons = [...buttons,button];
        }
        this.render(data);
    },


    deleteButton(nodeId:string,buttonId:string){
        //@ts-ignore
        const data = window.lfRef?.current.getGraphData();
        const nodes = data.nodes;
        const getNode = (nodeId: String) => {
            for (const node of nodes) {
                if (node.id === nodeId) {
                    return node;
                }
            }
        }
        const node = getNode(nodeId);
        const buttons =  node.properties.buttons || [];
        node.properties.buttons = buttons.filter((item:any)=>item.id !== buttonId);
        this.render(data);
    },

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
