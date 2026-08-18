import {loadFiles, upload} from "@/api/oss";
import {createOSSUtils} from "@springboot-framework/shared";

const OSSUtils = createOSSUtils({loadFiles, upload});

export default OSSUtils;
