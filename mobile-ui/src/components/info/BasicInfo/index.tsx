import React from "react";
import {Image} from "antd-mobile";
import "./index.scss";

const BasicInfo = () => {

    return (
        <div className={"basic-info"}>
            <div className={"basic-header-title"}>基本信息</div>
            <div className={"basic-header-icon"}>
                <div className={"title"}>个人头像</div>
                <div className={"value"}>
                    <Image
                        className={"img-icon"}
                        fit='cover'
                        style={{ borderRadius: 4 }}
                        src={'https://img02.mockplus.cn/rp/image/2025-02-14/1d050570-eaa0-11ef-9b3b-6ba737a6a7c2.jpg'}
                    />
                </div>
            </div>
            <div className={"basic-header-attr"}>
                <div className={"title"}>姓名</div>
                <div className={"value"}>
                    章三
                </div>
            </div>
        </div>
    )
}

export default BasicInfo;
