import React, {useContext, useEffect} from "react";
import Form from "@/components/form";
import FormTextArea from "@/components/form/textarea";
import {FlowViewReactContext} from "@/components/flow/view";


const FlowOpinion = ()=>{

    const flowViewReactContext = useContext(FlowViewReactContext);
    const opinionAction = flowViewReactContext?.opinionAction;
    const flowViewContext = flowViewReactContext?.flowViewContext;

    useEffect(() => {
        opinionAction?.current?.setFieldValue("advice", flowViewContext?.getOpinionAdvice());
    }, []);

    return (
        <>
            <Form
                actionRef={opinionAction}
            >
                <FormTextArea
                    name={"advice"}
                    label={"审批意见"}
                    textAreaRows={2}
                    required={true}
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
