import React from "react";
import {Button} from "antd";
import Page from "@/components/Layout/Page";


const Role = () => {

    return (
        <Page enablePageContainer>
            <h3 role-key={"title"}>AccessProvider Test Page</h3>

            <div role-key={"roles"}>
                <Button type="primary">Admin Button</Button>
            </div>
        </Page>
    )
}

export default Role;