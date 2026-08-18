import {captcha} from "@/api/account";
import {createCaptchaUtils} from "@springboot-framework/shared";

const CaptchaUtils = createCaptchaUtils({captcha});

export default CaptchaUtils;
