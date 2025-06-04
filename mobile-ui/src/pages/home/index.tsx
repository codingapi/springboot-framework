import {Button, Grid, Swiper, Toast} from "antd-mobile";
import React from "react";
import {useNavigate} from "react-router";
import Header from "@/layout/Header";
import {clearUser} from "@/api/account";
import "./index.scss";

const HomePage = () => {
    const colors = ['#ace0ff', '#bcffbd', '#e4fabd', '#ffcfac'];

    const navigate = useNavigate();

    const items = colors.map((color, index) => (
        <Swiper.Item key={index}>
            <div
                className="content"
                style={{background: color}}
                onClick={() => {
                    Toast.show(`你点击了卡片 ${index + 1}`)
                }}
            >
                {index + 1}
            </div>
        </Swiper.Item>
    ));


    return (
        <div>
            <Header isHome={true}>首页</Header>
            <Swiper>{items}</Swiper>
            <Grid
                columns={3}
                gap={[24, 24]}
                style={{
                    marginTop: 10
                }}
            >
                <Grid.Item>
                    <Button
                        style={{
                            width:"100%"
                        }}
                        onClick={() => {
                            navigate("/form")
                        }}
                    >表单</Button>
                </Grid.Item>

                <Grid.Item>
                    <Button
                        style={{
                            width:"100%"
                        }}
                        onClick={() => {
                            navigate("/mirco")
                        }}
                    >微前端</Button>
                </Grid.Item>

                <Grid.Item>
                    <Button
                        style={{
                            width:"100%"
                        }}
                        onClick={() => {
                            navigate("/leave/index")
                        }}
                    >请假管理</Button>
                </Grid.Item>


                <Grid.Item>
                    <Button
                        style={{
                            width:"100%"
                        }}
                        onClick={() => {
                            navigate("/flow/list")
                        }}
                    >待办中心</Button>
                </Grid.Item>


                <Grid.Item>
                    <Button
                        style={{
                            width:"100%"
                        }}
                        onClick={()=>{
                            clearUser();
                            navigate("/login")
                        }}
                    >退出登陆</Button>
                </Grid.Item>

            </Grid>

        </div>
    )
}

export default HomePage;
