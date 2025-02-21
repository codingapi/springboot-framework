import React, {useContext} from "react";
import Form from "@/components/form";
import FormTextArea from "@/components/form/textarea";
import {FlowViewReactContext} from "@/components/flow/view";


const FlowOpinion = ()=>{

    const flowViewReactContext = useContext(FlowViewReactContext);
    if (!flowViewReactContext) {
        return <></>;
    }
    const opinionAction = flowViewReactContext.opinionAction;

    return (
        <>
            <Form
                actionRef={opinionAction}
            >
                <FormTextArea
                    name={"opinion"}
                    label={"审批意见"}
                    textAreaRows={2}
                    validateFunction={async (content)=>{
                        const value = content.value;
                        if(value){
                            return [];
                        }
                        return ["请输入审批意见"];
                    }}
                />
            </Form>
        </>
    )
}

export default FlowOpinion;
