import {Button, Grid, Swiper, Toast} from "antd-mobile";
import React from "react";
import "./index.scss";
import {useNavigate} from "react-router";
import Header from "@/layout/Header";

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
                        onClick={() => {
                            navigate("/person/education/index")
                        }}
                    >教育信息</Button>
                </Grid.Item>

                {Array.from({length: 21}).map((item, index) => {
                    return (
                        <Grid.Item>
                            <Button>button{index + 1}</Button>
                        </Grid.Item>
                    )
                })}
            </Grid>

        </div>
    )
}

export default HomePage;
