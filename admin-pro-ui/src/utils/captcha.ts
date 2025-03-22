import {captcha} from "@/api/account";

class CaptchaUtils {

    static refresh = async () => {
        const res = await captcha();
        if (res.success) {
            return {
                url: res.data.captcha,
                code: res.data.key
            }
        }
        return null;
    }
}

export default CaptchaUtils;
