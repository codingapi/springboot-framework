import {FlowStore, UserSelectMode} from "@/components/flow/store/FlowSlice";
import {FlowResultMessage} from "@/components/flow/types";

/**
 * 流程的状态数据上下文对象
 */
export class FlowStateContext {

    private currentState: FlowStore;
    private readonly updateFlowStore: (currentState: any) => any;

    constructor(currentState: FlowStore, updateFlowStore: (state: any) => any) {
        this.currentState = JSON.parse(JSON.stringify(currentState));
        this.updateFlowStore = updateFlowStore;
    }

    private updateState() {
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

    setRecordId = (recordId: string) => {
        this.currentState = {
            ...this.currentState,
            recordId
        }
        this.updateState();
    }

    setResult = (result: FlowResultMessage) => {
        this.currentState = {
            ...this.currentState,
            result
        }
        this.updateState();
    }

    clearResult = () => {
        this.currentState = {
            ...this.currentState,
            result: null
        }
        this.updateState();
    }

    hasRecordId = () => {
        return this.currentState.recordId;
    }

    getRecordId = () => {
        return this.currentState.recordId;
    }

    setPostponedVisible(visible: boolean) {
        this.currentState = {
            ...this.currentState,
            postponedVisible: visible
        }
        this.updateState();
    }

    setUserSelectVisible(visible: boolean) {
        this.currentState = {
            ...this.currentState,
            userSelectVisible: visible
        }
        this.updateState();
    }

    setUserSelectMode(userSelectMode: UserSelectMode) {
        this.currentState = {
            ...this.currentState,
            userSelectVisible: true,
            userSelectMode: userSelectMode
        }
        this.updateState();
    }
}
