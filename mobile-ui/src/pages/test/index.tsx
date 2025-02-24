import React, {useImperativeHandle} from "react";
import {Button} from "antd-mobile";

export const MyContext = React.createContext<string|null>(null);

interface ChildAction{
    getValue:()=>string;
}

interface ChildProps {
    title: string;
    onFinish?:(values:string)=>void;
    childAction?:React.Ref<ChildAction>;
}

const Child: React.FC<ChildProps> = (props) => {

    const myContext = React.useContext(MyContext);

    const [value,setValue] = React.useState('');

    useImperativeHandle(props.childAction,()=> {
        return {
            getValue:()=>{
                return value;
            }
        }
    },[value]);

    return (
        <>
            <div>{props.title}</div>
            <div>{myContext}</div>
            <input value={value} onChange={(e)=>{
                setValue(e.target.value);
            }}/>
            <Button
                onClick={()=>{
                    props.onFinish && props.onFinish(value);
                }}
            >ok</Button>
        </>

    )
}

const Test = () => {
    const value = 'Hello, Context!';

    const childAction = React.useRef<ChildAction>(null);

    return (
        <MyContext.Provider value={value}>

           <div>
               <Child
                   childAction={childAction}
                   title={"title"}
                   onFinish={(values)=>{
                       console.log(values);
                   }}
               />
           </div>

            <Button
                onClick={()=>{
                    const value = childAction.current?.getValue();
                    console.log(value);
                }}
            >test</Button>
        </MyContext.Provider>

    )
}

export default Test
