import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Image, ImageUploader, ImageUploadItem as AntImageUploadItem, ImageViewer} from "antd-mobile";
import formFieldInit from "@/components/form/common";
import {CloseCircleFill} from "antd-mobile-icons";
import "./form.scss";
import FormInstance from "@/components/form/domain/FormInstance";

const fileToBase64 = (file: File): Promise<string> => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result as string);
        reader.onerror = error => reject(error);
    });
}

interface UploaderProps {
    name: string;
    formInstance?: FormInstance;
    uploaderAccept?: string;
    uploaderMaxCount?: number;
    value?: any;
    onChange?: (value: any, form?: FormInstance) => void;
    // 文件上传事件
    onUploaderUpload?: (filename: string, base64: string) => Promise<{
        // 文件id
        id: string,
        // 文件名
        name: string
        // 文件地址
        url: string;
    }>
    // 文件加载事件
    onUploaderLoad?: (ids: string) => Promise<{
        // 文件id
        id: string,
        // 文件名
        name: string
        // 文件地址
        url: string;
    }[]>
}

interface ImageUploadItem extends AntImageUploadItem{
    id?:string;
    name?:string;
}

const Uploader: React.FC<UploaderProps> = (props) => {
    const accept = props.uploaderAccept || "image/*";

    const [visible, setVisible] = React.useState(false);
    const formInstance = props.formInstance;

    const [fileList, setFileList] = React.useState<ImageUploadItem[]>([]);

    const [showImage, setShowImage] = React.useState<string>('');

    const handlerUploader = async (file: File) => {
        const base64 = await fileToBase64(file);
        const filename = file.name;

        if(props.onUploaderUpload ){
            return await props.onUploaderUpload(filename, base64);
        }else {
            return {
                url: URL.createObjectURL(file)
            }
        }
    }

    const reloadFiles = () => {
        if (props.value) {
            if(props.onUploaderLoad ){
                props.onUploaderLoad(props.value).then(res => {
                    if(res) {
                        setFileList(res.map((item: any) => {
                            return {
                                url: item.url,
                                id: item.id,
                                name: item.name
                            }
                        }))
                    }
                });
            }
        }
    }

    useEffect(() => {
        reloadFiles();
    }, [props.value]);

    const handlerDeleteFile = (file: any) => {
        // 删除文件
        const updatedFileList = fileList.filter((item: any) => item.id !== file.id);
        setFileList(updatedFileList);

        // 更新表单字段
        const currentValue = updatedFileList?.map((item: any) => item.id).join(",");
        formInstance?.setFieldValue(props.name, currentValue);
        props.onChange && props.onChange(currentValue, formInstance);
    };

    return (
        <>
            <ImageViewer
                image={showImage}
                visible={visible}
                onClose={() => {
                    setVisible(false)
                }}
            />
            <ImageUploader
                maxCount={props.uploaderMaxCount}
                accept={accept}
                value={fileList}
                onChange={(fileList) => {
                    const currentValue = fileList?.map((item: any) => item.id).join(",");
                    formInstance && formInstance?.setFieldValue(props.name, currentValue);
                    props.onChange && props.onChange(currentValue, formInstance);
                    setFileList(fileList);
                }}
                upload={handlerUploader as any}
                renderItem={(originNode, file: any, fileList) => {
                    if (accept === "image/*") {
                        return (
                            <div className={"form-uploader-file"}>
                                <CloseCircleFill
                                    className={"delete-icon"}
                                    onClick={() => handlerDeleteFile(file)}
                                />
                                <Image
                                    src={file.url}
                                    width={80}
                                    onClick={() => {
                                        setShowImage(file.url);
                                        setVisible(true);
                                    }}
                                />
                            </div>
                        )
                    }
                    return (
                        <div className={"form-uploader-file"}>
                            <CloseCircleFill
                                className={"delete-icon"}
                                onClick={() => handlerDeleteFile(file)}
                            />
                            <a href={file.url}>{file.name}</a>
                        </div>
                    )
                }}
            />
        </>
    )
}

const FormUploader: React.FC<FormItemProps> = (props) => {
    const {formContext, rules} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            rules={rules}
            hidden={props.hidden}
            help={props.help}
            disabled={props.disabled}
        >
            <Uploader
                name={props.name}
                value={props.value}
                formInstance={formContext}
                {...props}
            />
        </Form.Item>
    )
}

export default FormUploader;
