import React from "react";
import {registerComponent} from "@/framework/ComponentBus";

const MyItem = (props: { title: string, onChange: (value: string) => void }) => {
    return <input value={props.title} onChange={(e) => {
        props.onChange(e.target.value)
    }}/>
}

registerComponent('myItem', MyItem);
