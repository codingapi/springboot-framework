export interface EventTrigger {
    trigger: (eventKey: string) => void
}

// 流程事件触发器上下文对象
export class FlowTriggerContext {

    private list: EventTrigger[] = [];

    constructor() {
    }

    // 添加触发器
    addTrigger(trigger:(eventKey:string)=>void) {
        this.list.push({
            trigger
        });
    }

    // 触发事件
    triggerEvent(eventKey: string) {
        this.list.forEach(trigger => {
            trigger.trigger(eventKey);
        })
    }

    clear() {
        this.list = [];
    }

}

