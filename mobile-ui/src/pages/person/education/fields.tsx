import {FormField} from "@/components/form/types";
import CustomFormEditOption from "@/pages/person/education/custom";
import OSSUtils from "@/utils/oss";

export const loadFields = async (state: any, setState: (state: any) => void) => {
    return [
        {
            type: 'input',
            props: {
                name: 'name',
                label: '姓名',
                placeholder: '请输入姓名',
                inputMaxLength: 10,
                required: true,
                validateFunction: async (content) => {
                    if (content.value) {
                        return [];
                    } else {
                        return ["姓名不能为空"];
                    }
                },
                onChange: (value, form) => {
                    setState({
                        ...state,
                        name: value
                    })
                    form?.setFieldValue('type', 'Shuxue');
                    form?.reloadOptions('type');
                }
            },
        },
        {
            type: 'uploader',
            props: {
                name: 'avatar',
                label: '头像',
                placeholder: '请输入头像',
                help: '上传头像信息',
                onUploaderLoad:OSSUtils.loadFile,
                onUploaderUpload:OSSUtils.uploadFile
            },
        },
        {
            type: 'select',
            props: {
                name: "type",
                label: '学科类别',
                placeholder: '请输入学科类别',
                selectMultiple: false,
                selectOptionFormEditable: true,
                selectOptionFormEditView: CustomFormEditOption,
                onSelectOptionFormFinish: (formAction, selectOptionFormEditFormAction,
                                           reloadOption, close) => {
                    const values = selectOptionFormEditFormAction.getFieldsValue();
                    console.log('values', values);
                    const type = values['type'];
                    formAction.setFieldValue('type', type);
                    close && close();
                },
                loadOptions: async (form) => {
                    const name = form?.getFieldValue('name');
                    if (name) {
                        return [
                            {
                                label: '数学',
                                value: 'Shuxue',
                            },
                            {
                                label: '哲学',
                                value: 'Zhexue',
                            },
                            {
                                label: '计算机',
                                value: 'JiSuanJi',
                            }
                        ]
                    }
                    return [
                        {
                            label: '哲学',
                            value: 'Zhexue',
                        },
                        {
                            label: '计算机',
                            value: 'JiSuanJi',
                        }
                    ]
                },
                validateFunction: async (content) => {
                    const value = content.form.getFieldValue('type');
                    console.log('validateFunction type value', content.value, value);
                    if (content.value && content.value.length > 0) {
                        return [];
                    } else {
                        return ["学科类别不能为空"];
                    }
                }
            }
        },
        {
            type: 'select',
            props: {
                name: "major",
                label: '专业名称',
                placeholder: '请输入专业名称',
                selectMultiple: true,
                loadOptions: async (form) => {
                    const name = form?.getFieldValue('name');
                    return [
                        {
                            label: '大学',
                            value: 'Unit',
                            children: [
                                {
                                    label: '北京大学',
                                    value: 'Beijing',
                                    children: [
                                        {
                                            label: '清华大学',
                                            value: 'singHua',
                                            children: [
                                                {
                                                    label: '清华大学1',
                                                    value: 'singHua1'
                                                },
                                                {
                                                    label: '北京大学2',
                                                    value: 'beiDa1',
                                                    children: [
                                                        {
                                                            label: '清华大学11',
                                                            value: 'singHua11'
                                                        },
                                                        {
                                                            label: '北京大学21',
                                                            value: 'beiDa11',
                                                            children: [
                                                                {
                                                                    label: '清华大学111',
                                                                    value: 'singHua111'
                                                                },
                                                                {
                                                                    label: '北京大学211',
                                                                    value: 'beiDa111',
                                                                    children: [
                                                                        {
                                                                            label: '清华大学311',
                                                                            value: 'singHua311'
                                                                        },
                                                                        {
                                                                            label: '北京大学321',
                                                                            value: 'beiDa311'
                                                                        }
                                                                    ]
                                                                }
                                                            ]
                                                        },
                                                    ]
                                                }
                                            ]
                                        },
                                        {
                                            label: '北京大学',
                                            value: 'beiDa'
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }

            }
        },
        {
            type: 'date',
            props: {
                name: "date",
                label: '入学日期',
                placeholder: '请输入入学日期',
                onChange: (value, form) => {
                    console.log('value', value);
                    const name = form?.getFieldValue('name');
                    console.log('name', name);
                },
                validateFunction: async (content) => {
                    if (content.value) {
                        return [];
                    } else {
                        return ["入学日期不能为空"];
                    }
                }
            }
        },
        {
            type: 'date',
            props: {
                name: "year",
                label: '入学年份',
                dateFormat: 'YYYY',
                datePrecision: 'year',
                placeholder: '请输入入学年份',
                onChange: (value, form) => {
                    console.log('value', value);
                    const name = form?.getFieldValue('name');
                    console.log('name', name);
                }
            }
        },
        {
            type: 'radio',
            props: {
                name: "sex",
                label: '性别',
                placeholder: '请输入性别',
                options: [
                    {
                        label: '男',
                        value: 1
                    },
                    {
                        label: '女',
                        value: 0
                    }
                ],
                validateFunction: async (content) => {
                    if (content.value || content.value === 0) {
                        return [];
                    } else {
                        return ["性别不能为空"];
                    }
                }
            }
        },
        {
            type: 'textarea',
            props: {
                name: "desc",
                label: '简介',
                placeholder: '请输入简介',
                textAreaRows: 3,
                validateFunction: async (content) => {
                    if (content.value) {
                        return [];
                    } else {
                        return ["简介不能为空"];
                    }
                }
            }
        },
        {
            type: 'switch',
            props: {
                name: "memory",
                label: '婚姻状态',
                placeholder: '是否结婚',
                switchCheckText: '已婚',
                switchUnCheckText: '未婚',
                validateFunction: async (content) => {
                    if (content.value !== undefined) {
                        return [];
                    } else {
                        return ["婚姻状态不能为空"];
                    }
                }
            }
        },
        {
            type: 'checkbox',
            props: {
                name: "hobby",
                label: '爱好',
                placeholder: '请输入爱好',
                options: [
                    {
                        label: '篮球',
                        value: "1"
                    },
                    {
                        label: '足球',
                        value: "0"
                    }
                ],
                validateFunction: async (content) => {
                    if (content.value !== undefined) {
                        if (content.value.length == 0) {
                            return ["爱好不能为空"];
                        }
                        return [];
                    } else {
                        return ["爱好不能为空"];
                    }
                }
            }
        },
        {
            type: 'stepper',
            props: {
                name: "age",
                label: '年龄',
                validateFunction: async (content) => {
                    if (content.value !== undefined) {
                        return [];
                    } else {
                        return ["年龄不能为空"];
                    }
                }
            }
        },
        {
            type: 'slider',
            props: {
                name: "slider",
                label: '喜欢的温度',
                sliderMinNumber: 0,
                sliderMaxNumber: 100,
                sliderTicks: true,
                sliderPopover: true,
                sliderMarks: {
                    0: 0,
                    20: 20,
                    40: 40,
                    60: 60,
                    80: 80,
                    100: 100,
                },
                validateFunction: async (content) => {
                    if (content.value !== undefined) {
                        return [];
                    } else {
                        return ["喜欢的温度不能为空"];
                    }
                }
            }
        },
        {
            type: 'rate',
            props: {
                name: "movie",
                label: '电影评价',
                validateFunction: async (content) => {
                    if (content.value !== undefined) {
                        return [];
                    } else {
                        return ["电影评价不能为空"];
                    }
                }
            }
        },
        {
            type: 'selector',
            props: {
                name: "selector",
                label: '电影偏好',
                selectorColumn: 2,
                selectorMultiple: true,
                options: [
                    {
                        label: '动作',
                        value: 'action',
                    },
                    {
                        label: '喜剧',
                        value: 'comedy',
                    },
                    {
                        label: '爱情',
                        value: 'love',
                    },
                    {
                        label: '科幻',
                        value: 'sci-fi',
                    },
                ],
                validateFunction: async (content) => {
                    if (content.value !== undefined) {
                        return [];
                    } else {
                        return ["电影偏好不能为空"];
                    }
                }
            }
        },
        {
            type: 'cascader',
            props: {
                name: "location",
                label: '所在地区',
                validateFunction: async (content) => {
                    if (content.value !== undefined) {
                        return [];
                    } else {
                        return ["电影评价不能为空"];
                    }
                },
                loadOptions: async (form) => {
                    return [
                        {
                            label: "山东",
                            value: "Shandong",
                            children: [
                                {
                                    label: "济南",
                                    value: "Jinan"
                                }
                            ]
                        },
                        {
                            label: "广东",
                            value: "Guangdong",
                            children: [
                                {
                                    label: "广州",
                                    value: "Guangzhou"
                                }
                            ]
                        }
                    ]
                }
            }
        },
    ] as FormField[];
}
