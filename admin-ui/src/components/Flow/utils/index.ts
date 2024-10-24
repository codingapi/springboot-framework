const FlowUtils = {

    getEdges(nodeId:String){
        //@ts-ignore
        const data = window.lfRef?.current.getGraphData();
        const list = []
        if(data && data.edges){
            const edges = data.edges;
            for(const edge of edges){
                if(edge.sourceNodeId === nodeId){
                    list.push(edge);
                }
            }
        }
        return list;
    },

    render(data:any){
        //@ts-ignore
        window.lfRef?.current.render(data);
    }
}


export default FlowUtils;
