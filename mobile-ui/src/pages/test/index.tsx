import React from "react";

const MyContext = React.createContext('默认值');


const Child = ()=>{

    const contextValue = React.useContext(MyContext);

    return (
        <div>Child--- {contextValue}</div>
    )
}

const Test = ()=>{
    const value = 'Hello, Context!';

    return (
        <MyContext.Provider value={value}>
            test
            <Child />
        </MyContext.Provider>

    )
}

export default Test
