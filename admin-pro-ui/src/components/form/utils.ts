import {FormValidateContent} from "@/components/form/validate";

class ValidateUtils {

    /**
     * 非空校验
     * @param content 校验对象
     * @param message 错误信息
     */
    static validateNotEmpty = async (content: FormValidateContent, message?: string) => {
        const field = content.getFieldProps();
        const errorMessage = message ? message : field?.props.label + '不能为空' || '不能为空';
        const value = content.value;
        if (value) {
            if (Array.isArray(value)) {
                if (value.length === 0) {
                    return [errorMessage];
                }else {
                    return [];
                }
            } else {
                return [];
            }
        } else {
            return [errorMessage];
        }
    }
}

export default ValidateUtils;
