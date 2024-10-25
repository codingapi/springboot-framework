import React from "react";
import {Modal} from "antd";
import {detail} from "@/api/flow";


interface FlowDetailProps {
    id:any;
    visible:boolean;
    setVisible:(visible:boolean)=>void;
}

const FlowDetail:React.FC<FlowDetailProps> = (props)=>{

    const handleDetail = ()=>{
        console.log('load detail',props.id);
        detail(props.id).then(res=>{
            console.log(res);
        });
    }


    React.useEffect(() => {

        if(props.visible){
            handleDetail();
        }

    }, [props.visible]);

    return (
        <Modal
            title={"流程详情"}
            width={"80%"}
            open={props.visible}
            onClose={()=>{
                props.setVisible(false);
            }}
            onCancel={()=>{
                props.setVisible(false);
            }}
            onOk={()=>{
                props.setVisible(false);
            }}
        >

        </Modal>
    )
}


export default FlowDetail;
