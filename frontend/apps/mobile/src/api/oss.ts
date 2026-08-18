import {httpClient} from "@/api";
import {createOssApi} from "@springboot-framework/shared";

export const {loadFiles, upload} = createOssApi(httpClient);
