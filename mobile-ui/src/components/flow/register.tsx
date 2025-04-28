import {PostponedFormViewKey} from "@codingapi/ui-framework";
import {UserSelectFormViewKey} from "@codingapi/ui-framework";
import {ComponentBus} from "@codingapi/ui-framework";
import PostponedFormView from "@/components/flow/PostponedFormView";
import UserSelectFormView from "@/components/flow/UserSelectFormView";
import {FlowApi, FlowApiContent} from "@codingapi/flow-mobile";
import * as flowApi from "@/api/flow";


ComponentBus.getInstance().registerComponent(PostponedFormViewKey,PostponedFormView);
ComponentBus.getInstance().registerComponent(UserSelectFormViewKey,UserSelectFormView);


class DefaultFlowApiImpl implements FlowApi{

    startFlow(body: any): Promise<any> {
        return flowApi.startFlow(body);
    }

    submitFlow(body: any): Promise<any> {
        return flowApi.submitFlow(body);
    }

    removeFlow(body: any): Promise<any> {
        return flowApi.removeFlow(body);
    }

    saveFlow(body: any): Promise<any> {
        return flowApi.saveFlow(body);
    }

    postponedFlow(body: any): Promise<any> {
        return flowApi.postponed(body);
    }

    triggerCustomFlow(body: any): Promise<any> {
        return flowApi.custom(body);
    }

    transferFlow(body: any): Promise<any> {
        return flowApi.transfer(body);
    }

    urgeFlow(body: any): Promise<any> {
        return flowApi.urge(body);
    }

    recallFlow(body: any): Promise<any> {
        return flowApi.recall(body);
    }

    trySubmitFlow(body: any): Promise<any> {
        return flowApi.trySubmitFlow(body);
    }

    getDetailById(id: string): Promise<any> {
        return flowApi.detail(id,null);
    }

    getDetailByWorkCode(workCode: string): Promise<any> {
        return flowApi.detail(null,workCode);
    }

}

FlowApiContent.getInstance().registerFlowApi(new DefaultFlowApiImpl());
