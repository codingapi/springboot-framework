import {FlowStore} from "@/components/flow/store/FlowSlice";

export class FlowStateContext {

    private readonly flowStore: FlowStore;
    private readonly updateFlowStore: (state: FlowStore) => void;
    private recordId: string;

    constructor(flowStore: FlowStore, updateFlowStore: (state: FlowStore) => void) {
        this.flowStore = flowStore;
        this.updateFlowStore = updateFlowStore;
        this.recordId = flowStore.recordId;
        console.log('recordId:', this.recordId);
    }


    setRequestLoading = (requestLoading: boolean) => {
        this.updateFlowStore({
            ...this.flowStore,
            requestLoading
        });
    }

    updateRecordId = (recordId: string) => {
        this.recordId = recordId;
        this.updateFlowStore({
            ...this.flowStore,
            recordId
        });
    }

    hasRecordId = () => {
        return this.flowStore.recordId !== '';
    }

    getRecordId = () => {
        return this.recordId || this.flowStore.recordId;
    }

}
