import {NamePath} from "rc-field-form/es/interface";
import {FormAction} from "@/components/form";

// 流程表单验证内容
export class FormValidateContent {
    readonly value: any;
    readonly name: NamePath;
    readonly form: FormAction;

    constructor(value: any,name: NamePath, form: FormAction) {
        this.value = value;
        this.name = name;
        this.form = form;
    }

    getFieldProps = () => {
        return this.form.getFieldProps(this.name);
    }
}

// 自定义验证上下文
export class FormValidateContext {

    private readonly map: Map<NamePath, (content: FormValidateContent) => Promise<string[]>>;

    constructor() {
        this.map = new Map<NamePath, (content: FormValidateContent) => Promise<string[]>>();
    }

    public addValidateFunction(name:NamePath,validateFunction: (content: FormValidateContent) => Promise<string[]>) {
        this.map.set(name, validateFunction);
    }

    public clear(){
        this.map.clear();
    }

    public getValidate(name:NamePath){
        return this.map.get(name);
    }


    public validateField =  (name:NamePath,form: FormAction) => {
        return new Promise((resolve,reject)=>{
            const value = form.getFieldValue(name);
            const content = new FormValidateContent(value,name,form);
            const validateFunction = this.getValidate(name);
            if(validateFunction) {
                validateFunction(content)
                    .then((res) => {
                        form.setFields(
                            [
                                {
                                    name: name,
                                    errors: res,
                                }
                            ]
                        )
                        if (res.length > 0) {
                            resolve(false);
                        } else {
                            resolve(true);
                        }
                    })
                    .catch((error) => {
                        form.setFields(
                            [
                                {
                                    name: name,
                                    errors: [error],
                                }
                            ]
                        )
                        console.log('error', error)
                        reject(false);
                    })
            }
        });
    }

    public validate = async (form: FormAction) => {
        const list = Array.from(this.map.keys().map(item => {
           return this.validateField(item,form);
        }));

        const results = await Promise.all(list);
        return results.every((result) => result);
    }
}


