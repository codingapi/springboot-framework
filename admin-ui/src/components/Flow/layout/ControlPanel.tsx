import React from "react";
import {
    ArrowDownOutlined,
    ArrowLeftOutlined,
    ArrowRightOutlined,
    EnvironmentOutlined,
    MonitorOutlined,
    ZoomInOutlined,
    ZoomOutOutlined
} from "@ant-design/icons";
import "./ControlPanel.scss";
import {Tooltip} from "antd";


interface ControlProps {
    className: string,
    iconSize?: number,
    onZoomIn?: () => void,
    onZoomOut?: () => void,
    onZoomReset?: () => void,
    onUndo?: () => void,
    onRedo?: () => void,
    onMiniMap?: () => void,
    onDownload?: () => void,
}

const ControlPanel: React.FC<ControlProps> = (props) => {
    const iconSize = props.iconSize || 16;
    return (
        <div className={props.className}>
            <div className={"control-content"}>
                <Tooltip className={"control-item"} placement="top" title={"放大"}>
                    <ZoomInOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            props.onZoomIn?.();
                        }}
                    />
                </Tooltip>
                <Tooltip className={"control-item"} placement="top" title={"缩小"}>
                    <ZoomOutOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            props.onZoomOut?.();
                        }}
                    />
                </Tooltip>
                <Tooltip className={"control-item"} placement="top" title={"自适应"}>
                    <MonitorOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            props.onZoomReset?.();
                        }}
                    />
                </Tooltip>
                <Tooltip className={"control-item"} placement="top" title={"上一步"}>
                    <ArrowLeftOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            props.onUndo?.();
                        }}
                    />
                </Tooltip>
                <Tooltip className={"control-item"} placement="top" title={"下一步"}>
                    <ArrowRightOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            props.onRedo?.();
                        }}
                    />
                </Tooltip>
                <Tooltip className={"control-item"} placement="top" title={"小地图"}>
                    <EnvironmentOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            props.onMiniMap?.();
                        }}
                    />
                </Tooltip>
                <Tooltip className={"control-item"} placement="top" title={"下载图片"}>
                    <ArrowDownOutlined
                        style={{fontSize: iconSize}}
                        onClick={() => {
                            props.onDownload?.();
                        }}
                    />
                </Tooltip>

            </div>
        </div>
    )
}

export default ControlPanel;
