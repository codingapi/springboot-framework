import React, {useContext, useEffect} from "react";
import {Form} from "@codingapi/form-pc";
import {FlowViewReactContext} from "@/components/flow/view";

const FlowFormOpinion = ()=>{

    const flowViewReactContext = useContext(FlowViewReactContext);
    const opinionInstance = flowViewReactContext?.opinionInstance;
    const flowRecordContext = flowViewReactContext?.flowRecordContext;

    useEffect(() => {
        opinionInstance?.setFieldValue("advice", flowRecordContext?.getOpinionAdvice());
    }, []);

    return (
        <>
            <Form
                form={opinionInstance}
                layout={"vertical"}
                loadFields={async ()=>{
                    return [
                        {
                            type:'textarea',
                            props:{
                                name:"advice",
                                label:"审批意见",
                                textAreaRows:2,
                                required:true,
                                validateFunction:async (content)=>{
                                    const value = content.value;
                                    if(value){
                                        return [];
                                    }
                                    return ["请输入审批意见"];
                                }
                            }
                        }
                    ]
                }}
            >
            </Form>
        </>
    )
}

export default FlowFormOpinion;
