import {FlowStore} from "@/components/flow/store/FlowSlice";

export class FlowStateContext {

    private currentState: FlowStore;
    private readonly updateFlowStore: (currentState:any) => any;

    constructor(currentState: FlowStore, updateFlowStore: (state: any) => any) {
        this.currentState = JSON.parse(JSON.stringify(currentState));
        this.updateFlowStore = updateFlowStore;
    }

    private updateState(){
        this.updateFlowStore({
            ...this.currentState
        })
    }

    setRequestLoading = (requestLoading: boolean) => {
        this.currentState = {
            ...this.currentState,
            requestLoading
        }
        this.updateState();
    }

    updateRecordId = (recordId: string) => {
        this.currentState = {
            ...this.currentState,
            recordId
        }
        this.updateState();
    }

    hasRecordId = () => {
        return this.currentState.recordId;
    }

    getRecordId = () => {
        return this.currentState.recordId;
    }

}
