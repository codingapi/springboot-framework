import React, {useEffect} from "react";
import {Form, Modal} from "antd";
import {detail} from "@/api/flow";
import FlowTitle from "@/components/Flow/flow/FlowTitle";
import {FlowData} from "@/components/Flow/flow/data";
import FlowTabs from "@/components/Flow/flow/FlowTabs";
import {
    FlowFormParams,
    FlowFormView,
    FlowFormViewProps,
    PostponedFormProps,
    PostponedFormViewKey,
    ResultFormProps,
    ResultFormViewKey,
    UserSelectProps,
    UserSelectViewKey
} from "@/components/Flow/flow/types";
import {registerEvents} from "@/components/Flow/flow/events";
import {getComponent} from "@/framework/ComponentBus";
import {Provider, useDispatch, useSelector} from "react-redux";
import {
    clearPostponed,
    clearResult,
    clearUserSelect,
    FlowReduxState,
    flowStore,
    setSelectUsers,
    setTimeOut
} from "@/components/Flow/store/FlowSlice";
import "./index.scss";

interface FlowViewProps {
    // 流程编号
    id?: any;
    // 流程设计编号
    workCode?: string;
    // 是否显示
    visible: boolean;
    // 设置显示
    setVisible: (visible: boolean) => void;

    // 预览模式
    review?: boolean;

    // 表单视图
    view: React.ComponentType<FlowFormViewProps> | FlowFormView;
    // 表单参数，参数仅当在发起节点时才会传递
    formParams?: FlowFormParams;
}


const $FlowView: React.FC<FlowViewProps> = (props) => {

    // 流程表单
    const [viewForm] = Form.useForm();
    // 意见表单
    const [adviceForm] = Form.useForm();
    // 请求加载
    const [requestLoading, setRequestLoading] = React.useState(false);
    // 节点数据
    const [data, setData] = React.useState<any>(null);
    // 流程编号
    const [recordId, setRecordId] = React.useState<string>(props.id);

    // 延期表单展示框
    const postponedVisible = useSelector((state: FlowReduxState) => state.flow.postponedVisible);

    // 审批结果展示框
    const resultVisible = useSelector((state: FlowReduxState) => state.flow.resultVisible);
    // 审批结果
    const result = useSelector((state: FlowReduxState) => state.flow.result);
    // 是否关闭流程窗口
    const resultCloseFlow = useSelector((state: FlowReduxState) => state.flow.resultCloseFlow);

    // 选人展示框
    const userSelectVisible = useSelector((state: FlowReduxState) => state.flow.userSelectVisible);
    // 选人范围
    const specifyUserIds = useSelector((state: FlowReduxState) => state.flow.specifyUserIds);
    // 选人模式
    const userSelectMode = useSelector((state: FlowReduxState) => state.flow.userSelectMode);
    // 选人类型
    const userSelectType = useSelector((state: FlowReduxState) => state.flow.userSelectType);


    // flow store redux
    const dispatch = useDispatch();

    // 请求流程详情数据
    const loadFlowDetail = () => {
        if (props.id) {
            detail(props.id, null).then(res => {
                if (res.success) {
                    setData(res.data);
                }
            });
        }
        if (props.workCode) {
            detail(null, props.workCode).then(res => {
                if (res.success) {
                    setData(res.data);
                }
            });
        }
    }

    // 注册事件
    useEffect(() => {
        setData(null);
        setRecordId(props.id);
        if (props.visible) {
            loadFlowDetail();
        }
    }, [props.visible]);


    // 注册事件
    const handlerClicks = registerEvents(
        recordId,
        setRecordId,
        new FlowData(data, props.formParams),
        viewForm,
        adviceForm,
        setRequestLoading,
        () => {
            props.setVisible(false)
        }
    );

    if (!props.visible) {
        return null;
    }

    // 延期表单视图
    const PostponedFormView = getComponent(PostponedFormViewKey) as React.ComponentType<PostponedFormProps>;

    // 流程结果视图
    const ResultFormView = getComponent(ResultFormViewKey) as React.ComponentType<ResultFormProps>;

    // 用户选人视图
    const UserSelectView = getComponent(UserSelectViewKey) as React.ComponentType<UserSelectProps>;

    return (
        <Modal
            className="flow-Modal"
            width={"100%"}
            open={props.visible}
            onClose={() => {
                props.setVisible(false);
            }}
            onCancel={() => {
                props.setVisible(false);
            }}
            onOk={() => {
                props.setVisible(false);
            }}
            destroyOnClose={true}
            footer={false}
            closable={false}
            title={
                <FlowTitle
                    setVisible={props.setVisible}
                    flowData={new FlowData(data, props.formParams)}
                    requestLoading={requestLoading}
                    setRequestLoading={setRequestLoading}
                    handlerClick={(item: any) => {
                        handlerClicks(item);
                    }}
                />
            }
        >
            <FlowTabs
                flowData={new FlowData(data, props.formParams)}
                view={props.view}
                visible={props.visible}
                form={viewForm}
                adviceForm={adviceForm}
                review={props.review}
            />


            {PostponedFormView && (
                <PostponedFormView
                    visible={postponedVisible}
                    setVisible={() => {
                        dispatch(clearPostponed());
                    }}
                    onFinish={(values) => {
                        dispatch(setTimeOut(values.hours));
                    }}
                />
            )}

            {ResultFormView && (
                <ResultFormView
                    visible={resultVisible}
                    result={result}
                    flowCloseable={resultCloseFlow}
                    closeFlow={() => {
                        props.setVisible(false);
                    }}
                    setVisible={() => {
                        dispatch(clearResult());
                    }}
                />
            )}

            {UserSelectView && (
                <UserSelectView
                    visible={userSelectVisible}
                    setVisible={() => {
                        dispatch(clearUserSelect());
                    }}
                    onFinish={(values) => {
                        dispatch(setSelectUsers(values));
                    }}
                    specifyUserIds={specifyUserIds}
                    mode={userSelectMode}
                    userSelectType={userSelectType}
                />
            )}
        </Modal>
    )
}


const FlowView: React.FC<FlowViewProps> = (props) => {
    return (
        <Provider store={flowStore}>
            <$FlowView {...props} />
        </Provider>
    );
};

export default FlowView;
