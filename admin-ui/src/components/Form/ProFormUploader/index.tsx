import React from "react";
import {RcFile} from "antd/es/upload";
import {ProFormUploadButton, ProFormUploadButtonProps} from "@ant-design/pro-form";
import {rcFileToBase64} from "@/utils/base64";

const ProFormUploader: React.FC<ProFormUploadButtonProps> = (props) => {
    return (
        <ProFormUploadButton
            {...props}
            fieldProps={{
                customRequest: async ({file, onSuccess}) => {
                    const base64 = await rcFileToBase64(file as RcFile);
                    // @ts-ignore
                    onSuccess(base64);
                }
            }}
        />
    )
}

export default ProFormUploader;
