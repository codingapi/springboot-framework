import React from "react";
import {Button} from "antd-mobile";


interface InfoFooterProps{
    text:string;
}

const InfoFooter:React.FC<InfoFooterProps> = (props)=>{

    return (
        <div style={{
            backgroundColor:'white',
            paddingTop:10,
            paddingBottom:10,
            width:'100%',
            height:'40px'
        }}>
           <div style={{
               paddingLeft:'60px',
               paddingRight:'60px'
           }}>
               <Button
                   block={true}
                   color='primary'
                   size={'middle'}
               >
                   {props.text}
               </Button>
           </div>
        </div>
    )
}


export default InfoFooter;
