import {httpClient} from "@/api";
import {createAccountApi} from "@springboot-framework/shared";

export const {login, captcha, initUser, clearUser} = createAccountApi(httpClient);
