import React, {useEffect, useState} from "react";
import {FormItemProps} from "@/components/form/types";
import {Button, Form, Image, Upload} from "antd";
import formFieldInit from "@/components/form/common";
import {PlusOutlined, UploadOutlined} from "@ant-design/icons";
import {RcFile} from "antd/es/upload";
import {UploadFile} from "antd/lib";
import {FormAction} from "@/components/form/index";
import "./form.scss";

const fileToBase64 = (file: RcFile): Promise<string> => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result as string);
        reader.onerror = error => reject(error);
    });
}

const formToValue = (value: UploadFile[]) => {
    if (value && value.length > 0) {
        return value.map(item => {
            if (item.response) {
                return item.response.id;
            }
        }).join(',');
    }
    if(value && value.length==0){
        return '';
    }
    return value;
}

interface UploaderProps extends FormItemProps {
    formAction?: FormAction;
    uploaderAccept: string
}


const Uploader: React.FC<UploaderProps> = (props) => {
    const formAction = props.formAction;
    const isImage = props.uploaderAccept === 'image/*';

    const [fileList, setFileList] = React.useState<UploadFile[]>([]);
    const [previewImage, setPreviewImage] = useState('');
    const [previewOpen, setPreviewOpen] = useState(false);

    const reloadFiles = () => {
        if (props.value) {
            if (props.onUploaderLoad) {
                props.onUploaderLoad(props.value).then(res => {
                    setFileList(res.map((item: any) => {
                        return {
                            uid: item.id || '-1',
                            name: item.name,
                            status: 'done',
                            url: item.url,
                            response: {
                                id: item.id,
                                url: item.url,
                                name: item.name
                            },
                        }
                    }))
                });
            }
        }
    }

    useEffect(() => {
        reloadFiles();
    }, [props.value]);

    const handlePreview = async (file: UploadFile) => {
        setPreviewImage(file.response?.url);
        setPreviewOpen(true);
    };

    return (
        <div>
            <Upload
                disabled={props.disabled}
                name={props.name}
                fileList={fileList}
                accept={props.uploaderAccept}
                listType={isImage ? 'picture-card' : undefined}
                maxCount={props.uploaderMaxCount}
                customRequest={async ({file, onSuccess}) => {
                    const currentFile = file as RcFile;
                    const base64 = await fileToBase64(currentFile);
                    const filename = currentFile.name;
                    if (props.onUploaderUpload) {
                        const res = await props.onUploaderUpload(filename, base64);
                        if(res) {
                            const {url, id, name} = res;
                            // @ts-ignore
                            onSuccess({
                                url: url,
                                id: id,
                                name: name
                            });
                        }
                    } else {
                        const url = URL.createObjectURL(currentFile);
                        // @ts-ignore
                        onSuccess({
                            url: url,
                            id: Math.random(),
                            name: currentFile.name
                        });
                    }
                }}
                onChange={(info) => {
                    const fileList = info.fileList;
                    if(fileList.length>0 && fileList.every(item=>item.status==='done')) {
                        const currentValue = formToValue(fileList);
                        formAction?.setFieldValue(props.name, currentValue);
                        props.onChange && props.onChange(currentValue, formAction);
                    }
                    setFileList(fileList);
                }}
                onPreview={isImage ? handlePreview : undefined}
            >
                {isImage && (
                    <PlusOutlined/>
                )}
                {!isImage && (
                    <Button icon={<UploadOutlined/>}>选择文件</Button>
                )}

            </Upload>

            {isImage && previewImage && (
                <Image
                    wrapperStyle={{display: 'none'}}
                    preview={{
                        visible: previewOpen,
                        onVisibleChange: (visible) => setPreviewOpen(visible),
                        afterOpenChange: (visible) => !visible && setPreviewImage(''),
                    }}
                    src={previewImage}
                />
            )}
        </div>
    )
}

const FormUploader: React.FC<FormItemProps> = (props) => {
    const {formAction} = formFieldInit(props);
    const accept = props.uploaderAccept || "image/*";
    return (
        <Form.Item
            name={props.name}
            label={props.label}
            required={props.required}
            hidden={props.hidden}
            help={props.help}
        >
            <Uploader
                formAction={formAction}
                value={props.value}
                onChange={props.onChange}
                uploaderAccept={accept}
                {...props}
            />
        </Form.Item>
    )
}

export default FormUploader;
