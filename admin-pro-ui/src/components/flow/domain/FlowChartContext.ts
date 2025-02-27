import React from "react";
import {LogicFlow} from "@logicflow/core";

class FlowChartContext{

    private readonly lfRef:React.RefObject<LogicFlow>;

    constructor(lfRef:React.RefObject<LogicFlow>){
        this.lfRef = lfRef;
    }

    private generateUUID (){
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            const r = (Math.random() * 16) | 0;
            const v = c === 'x' ? r : (r & 0x3) | 0x8;
            return v.toString(16);
        });
    }

    addNode(type:string,name:string){
        const uid = this.generateUUID();
        this.lfRef.current?.dnd.startDrag({
            id: uid,
            type: type,
            properties: {
                id: uid,
                name: name,
            }
        })
    }
}

export default FlowChartContext;
