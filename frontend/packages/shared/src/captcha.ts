import type {Response} from "@codingapi/ui-framework";

export interface CaptchaResult {
    url: string;
    code: string;
}

export interface CaptchaApi {
    captcha: () => Promise<Response>;
}

export interface CaptchaUtilsClass {
    refresh: () => Promise<CaptchaResult | null>;
}

export const createCaptchaUtils = (captchaApi: CaptchaApi): CaptchaUtilsClass => {

    class CaptchaUtils {

        static refresh = async () => {
            const res = await captchaApi.captcha();
            if (res.success) {
                return {
                    url: res.data.captcha,
                    code: res.data.key
                }
            }
            return null;
        }
    }

    return CaptchaUtils;
}
