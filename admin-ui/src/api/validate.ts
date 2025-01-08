import {FormInstance} from "antd/es/form/hooks/useForm";
import {NamePath} from "rc-field-form/es/interface";

// 流程表单API 提供get post的能力
export interface FlowFormApi {
    get: (url: string, params?: any) => Promise<any>;
    post: (url: string, data: any) => Promise<any>;
}

// 流程表单验证内容
export class FlowFormValidateContent {
    readonly value: any;
    readonly form: FormInstance;
    readonly api?: FlowFormApi

    constructor(value: any, form: FormInstance, api?: FlowFormApi) {
        this.value = value;
        this.form = form;
        this.api = api;
    }
}

// 自定义验证
export interface FlowFormCustomValidate {
    name: NamePath;
    validate: (content: FlowFormValidateContent) => Promise<string[]>;
}

// 流程表单API上下文
export class FlowFormApiContext {

    private static readonly instance: FlowFormApiContext = new FlowFormApiContext();

    private api: FlowFormApi | undefined;

    private constructor() {
        this.api = undefined;
    }

    public static getInstance() {
        return FlowFormApiContext.instance;
    }

    public setApi(api: FlowFormApi) {
        this.api = api;
    }

    public getApi() {
        return this.api;
    }
}

// 自定义验证上下文
export class FlowFormCustomValidateContext {

    private readonly map: Map<NamePath, FlowFormCustomValidate>;

    constructor() {
        this.map = new Map();
    }

    public addValidate(validate: FlowFormCustomValidate) {
        this.map.set(validate.name, validate);
    }

    public addCustomFunctionCodeValidate(namePath:NamePath,validateFuncCode:string){
        const validateFunc = new Function('content', validateFuncCode);
        this.addValidate({
            name: namePath,
            validate: async (content) => {
                return validateFunc(content);
            }
        });
    }

    public validate(form: FormInstance) {
        this.map.values().forEach((validate) => {
            const value = form.getFieldValue(validate.name);
            const content = new FlowFormValidateContent(value, form, FlowFormApiContext.getInstance().getApi());
            validate.validate(content).then((res) => {
                form.setFields(
                    [
                        {
                            name: validate.name,
                            errors: res,
                        }
                    ]
                )
            }).catch((error) => {
                form.setFields(
                    [
                        {
                            name: validate.name,
                            errors: [error],
                        }
                    ]
                )
            });
        });
    }
}


