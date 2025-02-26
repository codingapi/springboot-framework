import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Image, ImageUploader, ImageUploadItem, ImageViewer} from "antd-mobile";
import formFieldInit from "@/components/form/common";
import {loadFiles, upload} from "@/api/oss";
import {FormAction} from "@/components/form";
import {CloseCircleFill} from "antd-mobile-icons";
import "./form.scss";

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
    formAction?: FormAction;
    uploaderAccept?: string;
    uploaderMaxCount?: number;
    value?: any;
    onChange?: (value: any, form?: FormAction) => void;
}

const Uploader: React.FC<UploaderProps> = (props) => {
    const accept = props.uploaderAccept || "image/*";

    const [visible, setVisible] = React.useState(false);
    const formAction = props.formAction;

    const [fileList, setFileList] = React.useState<ImageUploadItem[]>([]);

    const [showImage, setShowImage] = React.useState<string>('');

    const handlerUploader = async (file: File) => {
        const base64 = await fileToBase64(file);
        const filename = file.name;
        const response = await upload({
            name: filename,
            data: base64
        })
        if (response.success) {
            const data = response.data;
            const url = `/open/oss/${data.key}`;
            return {
                url: url,
                id: data.id,
                name: data.name
            }
        }
        return {
            url: URL.createObjectURL(file)
        }
    }

    const reloadFiles = () => {
        if (props.value) {
            loadFiles(props.value).then(res => {
                if (res.success) {
                    const list = res.data.list;
                    setFileList(list.map((item: any) => {
                        const url = `/open/oss/${item.key}`;
                        return {
                            url: url,
                            id: item.id,
                            name: item.name
                        }
                    }))
                }
            });
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
        formAction?.setFieldValue(props.name, currentValue);
        props.onChange && props.onChange(currentValue, formAction);
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
                    formAction && formAction?.setFieldValue(props.name, currentValue);
                    props.onChange && props.onChange(currentValue, formAction);
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
    const {formAction, rules} = formFieldInit(props);

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
                formAction={formAction}
                {...props}
            />
        </Form.Item>
    )
}

export default FormUploader;
