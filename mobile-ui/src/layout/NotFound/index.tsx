import React from "react";
import {Button, Result} from "antd-mobile";
import {useNavigate} from "react-router";
import {config} from "@/config/theme";

const Index = () => {
    const navigate = useNavigate();

    const goHome = () => {
        navigate(config.welcomePath, {replace: true});
    }

    return (
        <Result
            title="抱歉，没有找到界面"
            status={"error"}
            description={(
                <div>
                    <div>请检查您的地址是否正确</div>
                    <Button
                        onClick={goHome}
                    >返回首页</Button>
                </div>
            )}
        />
    )
}

export default Index;
