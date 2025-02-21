import React, {useEffect} from "react";
import {FormContext} from "@/components/form";
import {FormItemProps} from "@/components/form/types";

const formFieldInit = (props: FormItemProps,reloadOption?:()=>void) => {
    const formContext = React.useContext(FormContext) || undefined;
    const formAction = formContext?.formAction;
    const validateContext = formContext?.validateContext;
    const [random, setRandom] = React.useState(0);

    const rules: never[] = [];

    useEffect(() => {
        if (props.validateFunction) {
            if (validateContext) {
                if (props.disabled || props.hidden) {
                    // do nothing
                } else {
                    validateContext.addValidateFunction(props.name, props.validateFunction);
                }
            }
        }
        const reloadContext = formContext?.reloadContext;
        if (reloadContext) {
            reloadContext.addListener(props.name, () => {
                setRandom(Math.random);
            });
        }

        const optionContext = formContext?.optionContext;
        if (optionContext) {
            optionContext.addListener(props.name, () => {
                if(reloadOption){
                    reloadOption();
                }
            });
        }
    }, [formContext]);

    return {formAction, validateContext, rules};
}

export default formFieldInit;
