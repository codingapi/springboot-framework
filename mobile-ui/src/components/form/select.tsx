import React, {useEffect} from "react";
import {FormItemProps, FormOption} from "@/components/form/types";
import {CheckList, Form, Popup, SearchBar} from "antd-mobile";
import {RightOutline} from "antd-mobile-icons";
import formFieldInit from "@/components/form/common";
import "./form.scss";


const valueToForm = (value: string) => {
    if (value && value.length > 0) {
        return value.split(",");
    }
    return value;
}

const formToValue = (value: string[]) => {
    console.log('select', value);
    if (value && value.length > 0) {
        return value.join(",")
    }
    return value;
}

const FormSelect: React.FC<FormItemProps> = (props) => {

    const [visible, setVisible] = React.useState(false);
    const [searchText, setSearchText] = React.useState('');

    const {formAction, rules} = formFieldInit(props, () => {
        reloadOptions();
    });

    const currentValue = valueToForm(formAction?.getFieldValue(props.name)) as string[] || valueToForm(props.value) as string[] || [];

    const [selected, setSelected] = React.useState<string[]>(currentValue);

    // 当前页面展示的选项的数据，会随着树级目录进入子数据，从而更新数据
    const [options, setOptions] = React.useState(props.options);

    // 用于展示数据的缓存数据，在数据插入不在更新
    const [optionCaches, setOptionCaches] = React.useState(props.options);

    const [paths, setPaths] = React.useState<FormOption[]>([]);

    const CheckBoxValueText = () => {
        const currentValue = valueToForm(formAction?.getFieldValue(props.name)) as string[] || valueToForm(props.value) as string[] || [];
        const optionLabelFetch = (value: string) => {
            let label = value;
            let fetchState = false;
            const loadLabel = (item: FormOption) => {
                if (!fetchState) {
                    if (item.value === value) {
                        label = item.label;
                        fetchState = true;
                    }
                    if (item.children) {
                        item.children.forEach(child => {
                            loadLabel(child);
                        })
                    }
                }
            }

            optionCaches?.forEach(item => {
                loadLabel(item);
            })
            return label;
        }
        const displaySpan = (list: string[]) => {
            if (list.length > 0) {
                return (
                    <span
                        onClick={() => {
                            setVisible(true);
                        }}>
                {list &&
                    list.map(item => {
                        return optionLabelFetch(item);
                    }).join(",")
                }
            </span>
                )
            } else {
                return (
                    <span
                        className={"placeholder-span"}
                        onClick={() => {
                            setVisible(true);
                        }}>
                    {props.placeholder || "请选择"}
                </span>
                )
            }
        }
        return displaySpan(currentValue);
    }

    const CheckboxItem: React.FC<FormOption> = (item) => {
        if (item.children && item.children.length > 0) {
            return (
                <CheckList.Item
                    value={item.value}
                    disabled={item.disable}
                    readOnly={true}
                >
                    <div
                        className={"checkbox-parent"}
                        onClick={() => {
                            setPaths([...paths, item]);
                            setOptions(item.children);
                        }}
                    >
                        {item.label}
                        <RightOutline/>
                    </div>
                </CheckList.Item>
            )
        }
        return (
            <CheckList.Item
                value={item.value}
                disabled={item.disable}
            >
                {item.label}
            </CheckList.Item>
        )
    }

    const reloadOptions = () => {
        if (props.loadOptions) {
            props.loadOptions(formAction).then(list => {
                setOptions(list);
                setOptionCaches(list);
            });
        }
    }

    useEffect(() => {
        setPaths([]);
        reloadOptions();
    }, []);


    useEffect(() => {
        setSearchText('');
        if (visible) {
            setPaths([]);
            setSelected(currentValue);
            reloadOptions();
        }
    }, [visible]);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            rules={rules}
            hidden={props.hidden}
            help={props.help}
            disabled={props.disabled}
            extra={(
                <RightOutline
                    onClick={() => {
                        setVisible(true);
                    }}
                />
            )}
            getValueProps={(value) => {
                if (value) {
                    return {
                        value: valueToForm(value)
                    }
                }
                return value
            }}
        >
            <CheckBoxValueText/>

            <Popup
                visible={visible}
                className={"select-popup"}
                onMaskClick={() => {
                    setVisible(false)
                }}
                destroyOnClose={true}
            >
                <div className={"select-popup-header"}>
                    <a
                        onClick={() => {
                            setVisible(false)
                        }}
                    >取消</a>
                    <a
                        onClick={() => {
                            formAction?.setFieldValue(props.name, formToValue(selected));
                            props.onChange && props.onChange(selected, formAction);
                            setVisible(false);
                        }}
                    >确定</a>
                </div>
                <div className={"select-popup-search"}>
                    <SearchBar
                        placeholder='输入查询选项'
                        value={searchText}
                        onChange={v => {
                            setSearchText(v);
                        }}
                    />
                </div>
                {paths.length > 0 && (
                    <div className={"select-popup-navbar"}>
                        {paths.map((item, index) => {
                            return (
                                <div className={"select-popup-navbar-item"}>
                                    <a
                                        className={"span"}
                                        onClick={() => {
                                            setPaths(paths.slice(0, index + 1));
                                            setOptions(item.children);
                                        }}
                                    >{item.label}</a>
                                    <RightOutline className={"arrow"}/>
                                </div>
                            )
                        })}
                    </div>
                )}

                <div className={"select-popup-content"}>
                    <CheckList
                        className={"select-popup-content-list"}
                        value={selected}
                        onChange={(value) => {
                            const currentValue = value as string[];
                            setSelected(currentValue);
                            // 单选时，选中即关闭弹框
                            if (!props.selectMultiple) {
                                formAction?.setFieldValue(props.name, formToValue(currentValue));
                                props.onChange && props.onChange(formToValue(currentValue), formAction);

                                setVisible(false);
                            }
                        }}
                        multiple={props.selectMultiple}
                    >
                        {options
                            && options
                                .filter(item => {
                                    if (searchText) {
                                        if (item.value.toUpperCase().includes(searchText.toUpperCase())) {
                                            return true;
                                        }
                                        if (item.label.toUpperCase().includes(searchText.toUpperCase())) {
                                            return true;
                                        }
                                        return false;
                                    }
                                    return true;
                                }).map((item) => {
                                    return (
                                        <CheckboxItem {...item}/>
                                    )
                                })}
                    </CheckList>
                </div>
            </Popup>
        </Form.Item>
    )
}

export default FormSelect;
