import React from "react";
import {Routes} from "react-router";
import {loadLayoutRoutes} from "@/config/route";
import Footer from "@/layout/Footer";
import {Provider} from "react-redux";
import {layoutStore} from "@/sotre/LayoutSlice";


const $Layout = () => {
    return (
        <>
            <div>
                <Routes>
                    {loadLayoutRoutes()}
                </Routes>
            </div>

            <Footer/>
        </>
    )
}


const Layout = () => {
    return (
        <Provider store={layoutStore}>
            <$Layout/>
        </Provider>
    )
}

export default Layout;
