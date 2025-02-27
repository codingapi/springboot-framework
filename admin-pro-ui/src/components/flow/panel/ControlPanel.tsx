import React, {useContext, useEffect} from "react";
import {
    ArrowDownOutlined,
    ArrowLeftOutlined,
    ArrowRightOutlined,
    EnvironmentOutlined,
    MonitorOutlined,
    ZoomInOutlined,
    ZoomOutOutlined
} from "@ant-design/icons";
import {Tooltip} from "antd";
import {FlowContext} from "@/components/flow";
import "./ControlPanel.scss";

const ControlPanel = () => {
    const iconSize = 16;

    const flowContext = useContext(FlowContext);

    const [mapVisible, setMapVisible] = React.useState(false);

    useEffect(() => {
        if (mapVisible) {
            flowContext?.flowPanelContext.showMap();
        } else {
            flowContext?.flowPanelContext.hiddenMap();
        }
    }, [mapVisible]);

    return (
        <div className={"flow-panel-control"}>
            <div className={"flow-panel-control-content"}>
                <Tooltip className={"flow-panel-control-content-item"} placement="top" title={"放大"}>
                    <ZoomInOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            flowContext?.flowPanelContext.zoom(true);
                        }}
                    />
                </Tooltip>
                <Tooltip className={"flow-panel-control-content-item"} placement="top" title={"缩小"}>
                    <ZoomOutOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            flowContext?.flowPanelContext.zoom(false);
                        }}
                    />
                </Tooltip>
                <Tooltip className={"flow-panel-control-content-item"} placement="top" title={"自适应"}>
                    <MonitorOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            flowContext?.flowPanelContext.resetZoom();
                        }}
                    />
                </Tooltip>
                <Tooltip className={"flow-panel-control-content-item"} placement="top" title={"上一步"}>
                    <ArrowLeftOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            flowContext?.flowPanelContext.undo();
                        }}
                    />
                </Tooltip>
                <Tooltip className={"flow-panel-control-content-item"} placement="top" title={"下一步"}>
                    <ArrowRightOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            flowContext?.flowPanelContext.redo();
                        }}
                    />
                </Tooltip>
                <Tooltip className={"flow-panel-control-content-item"} placement="top" title={"小地图"}>
                    <EnvironmentOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            setMapVisible(!mapVisible);
                        }}
                    />
                </Tooltip>
                <Tooltip className={"flow-panel-control-content-item"} placement="top" title={"下载图片"}>
                    <ArrowDownOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            flowContext?.flowPanelContext.download();
                        }}
                    />
                </Tooltip>

            </div>
        </div>
    )
}

export default ControlPanel;
