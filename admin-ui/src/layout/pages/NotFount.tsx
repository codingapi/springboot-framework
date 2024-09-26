import React from "react";
import {Button, Result} from "antd";
import {useNavigate} from "react-router";
import {config} from "@/config/theme";
import {useDispatch} from "react-redux";
import {refresh} from "@/store/MenuSlice";

const NotFount = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const goHome = () => {
        navigate(config.welcomePath, {replace: true});
        dispatch(refresh());
    }

    return (
        <Result
            status="404"
            title="404"
            subTitle="Sorry, the page you visited does not exist."
            extra={(
                <Button
                    type="primary"
                    onClick={goHome}
                >
                    Back Home
                </Button>
            )}
        />
    )
}

export default NotFount;
