import React, {useEffect} from "react";
import {FormItemProps, FormOption} from "@/components/form/types";
import {Button, CheckList, Form as AntdForm, InfiniteScroll, Popup, PullToRefresh, SearchBar} from "antd-mobile";
import {RightOutline, SetOutline} from "antd-mobile-icons";
import formFieldInit from "@/components/form/common";
import Form from "@/components/form";
import "./form.scss";


const valueToForm = (value: string) => {
    if (value && value.length > 0) {
        return value.split(",");
    }
    return value;
}

const formToValue = (value: string[]) => {
    if (value && value.length > 0) {
        return value.join(",")
    }
    return value;
}

interface CheckboxItemProps {
    item: FormOption;
    paths: FormOption[];
    setPaths: (paths: FormOption[]) => void;
    setOptions: (options: FormOption[]) => void;

}

const CheckboxItem: React.FC<CheckboxItemProps> = (props) => {
    const {item, paths, setPaths, setOptions} = props;
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
                        setOptions(item.children || []);
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

interface CheckboxListViewProps {
    data: FormOption[];
    paths: FormOption[];
    setPaths: (paths: FormOption[]) => void;
    setOptions: (options: FormOption[]) => void;
}

const CheckboxListView: React.FC<CheckboxListViewProps> = (props) => {
    const {data} = props;
    const pageSize = 20;

    const [currentPage, setCurrentPage] = React.useState(1);

    const [list, setList] = React.useState<FormOption[]>([]);

    const [hasMore, setHasMore] = React.useState(true);

    const reload = () => {
        const currentPage = 1;
        if (data.length > 0) {
            const list = data.slice((currentPage - 1) * pageSize, currentPage * pageSize);
            setList(list);
            setHasMore(true);
        } else {
            setList([]);
            setHasMore(false);
        }
        setCurrentPage(currentPage);
    }

    const loadMore = () => {
        setCurrentPage(prevState => {
            const newPage = prevState + 1;
            if (newPage * pageSize >= data.length) {
                setHasMore(false);
            } else {
                setHasMore(true);
            }
            setList(prevState => {
                const list = data.slice((newPage - 1) * pageSize, newPage * pageSize);
                return [...prevState, ...list]
            });
            return newPage;
        });
    }


    useEffect(() => {
        reload();
    }, [props.data]);

    return (
        <>
            <PullToRefresh
                onRefresh={async () => {
                    reload();
                }}
            >
                {list && list.map((item: FormOption, index: number) => {
                    return <CheckboxItem key={index} item={item} {...props}/>
                })}

                {hasMore && (
                    <InfiniteScroll
                        loadMore={async () => {
                            loadMore();
                        }}
                        hasMore={hasMore}
                    />
                )}

            </PullToRefresh>
        </>
    )
}

const FormSelect: React.FC<FormItemProps> = (props) => {

    const [visible, setVisible] = React.useState(false);
    const [searchText, setSearchText] = React.useState('');

    const {formContext, rules} = formFieldInit(props, () => {
        reloadOptions();
    });

    const currentValue = valueToForm(formContext?.getFieldValue(props.name)) as string[] || valueToForm(props.value) as string[] || [];

    const [selected, setSelected] = React.useState<string[]>(currentValue);

    const [settingOptionVisible, setSettingOptionVisible] = React.useState(false);

    // 当前页面展示的选项的数据，会随着树级目录进入子数据，从而更新数据
    const [options, setOptions] = React.useState(props.options);

    // 用于展示数据的缓存数据，在数据插入不在更新
    const [optionCaches, setOptionCaches] = React.useState(props.options);

    const [paths, setPaths] = React.useState<FormOption[]>([]);

    const CheckBoxValueText = () => {
        const currentValue = valueToForm(formContext?.getFieldValue(props.name)) as string[] || valueToForm(props.value) as string[] || [];
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

    const reloadOptions = () => {
        if (props.loadOptions) {
            props.loadOptions(formContext).then(list => {
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

    const selectOptionFormEditInstance = Form.useForm();


    const handlerOptionFormFinish = () => {
        if (props.onSelectOptionFormFinish && selectOptionFormEditInstance && formContext) {
            props.onSelectOptionFormFinish(
                formContext,
                selectOptionFormEditInstance,
                reloadOptions,
                () => {
                    setSettingOptionVisible(false);
                    setVisible(false);
                }
            );
        }
    }

    return (
        <AntdForm.Item
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
                    {settingOptionVisible && (
                        <>
                            {props.selectOptionFormEditFooterOkText || "添加选项"}
                        </>
                    )}
                    <a
                        onClick={() => {
                            if (props.selectOptionFormEditable) {
                                handlerOptionFormFinish();
                            } else {
                                formContext?.setFieldValue(props.name, formToValue(selected));
                                props.onChange && props.onChange(selected, formContext);
                                setVisible(false);
                            }
                        }}
                    >确定</a>
                </div>
                {!settingOptionVisible && (
                    <div className={"select-popup-search"}>
                        <SearchBar
                            className={"select-popup-search-bar"}
                            placeholder='输入查询选项'
                            value={searchText}
                            onChange={v => {
                                setSearchText(v);
                            }}
                        />
                        {props.selectOptionFormEditable && (
                            <SetOutline
                                className={"select-popup-search-button"}
                                onClick={() => {
                                    setSettingOptionVisible(!settingOptionVisible);
                                }}
                            />
                        )}
                    </div>
                )}

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

                    {settingOptionVisible && (
                        <div className={"select-popup-content-custom-form"}>
                            {formContext && props.selectOptionFormEditView && (
                                <props.selectOptionFormEditView
                                    currentInstance={selectOptionFormEditInstance}
                                    formInstance={formContext}/>
                            )}
                            <div className={"select-popup-content-custom-footer"}>
                                <Button
                                    size={'middle'}
                                    color={'primary'}
                                    onClick={() => {
                                        handlerOptionFormFinish();
                                    }}
                                >{props.selectOptionFormEditFooterOkText || "添加"}</Button>
                                <Button
                                    size={'middle'}
                                    onClick={() => {
                                        setSettingOptionVisible(false);
                                    }}
                                >{props.selectOptionFormEditFooterOkText || "取消"}</Button>
                            </div>

                        </div>
                    )}

                    {!settingOptionVisible && (
                        <CheckList
                            className={"select-popup-content-list"}
                            value={selected}
                            onChange={(value) => {
                                const currentValue = value as string[];
                                setSelected(currentValue);
                                // 单选时，选中即关闭弹框
                                if (!props.selectMultiple) {
                                    formContext?.setFieldValue(props.name, formToValue(currentValue));
                                    props.onChange && props.onChange(formToValue(currentValue), formContext);

                                    setVisible(false);
                                }
                            }}
                            multiple={props.selectMultiple}
                        >
                            <CheckboxListView
                                data={options && options
                                        .filter((item => {
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
                                        }))
                                    || []}
                                setOptions={setOptions}
                                paths={paths}
                                setPaths={setPaths}
                            />
                        </CheckList>
                    )}
                </div>
            </Popup>
        </AntdForm.Item>
    )
}

export default FormSelect;
