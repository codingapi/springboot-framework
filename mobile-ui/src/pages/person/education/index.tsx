import React from "react";
import Header from "@/layout/Header";
import InfoList, {InfoItem} from "@/components/info/List";
import {useNavigate} from "react-router";
import Footer from "@/layout/Footer";
import {Button} from "antd-mobile";

const EducationIndex = () => {
    const navigate = useNavigate();

    const data = [
        {
            id: 1,
            state: 'done',
            attrs:{
                '学历': '大学本科',
                '类别': '全日制教育',
                '日期': '2021.9~2024.7',
            },
        },
        {
            id: 2,
            state: 'un_submit',
            attrs: {
                '学历': '硕士研究生',
                '类别': '全日制教育',
                '日期': '2024.9~2026.7',
            }
        },
        {
            id: 3,
            state: 'todo',
            attrs: {
                '学历': '博士研究生',
                '类别': '全日制教育',
                '日期': '2026.9~2030.7',
            }
        },
        {
            id: 4,
            state: 'un_submit',
            attrs: {
                '学历': '博士研究生',
                '类别': '全日制教育',
                '日期': '2026.9~2030.7',
            }
        },
        {
            id: 5,
            state: 'un_submit',
            attrs: {
                '学历': '博士研究生',
                '类别': '全日制教育',
                '日期': '2026.9~2030.7',
            }
        },
        {
            id: 6,
            state: 'un_submit',
            attrs: {
                '学历': '博士研究生',
                '类别': '全日制教育',
                '日期': '2026.9~2030.7',
            }
        },
        {
            id: 7,
            state: 'un_submit',
            attrs: {
                '学历': '博士研究生',
                '类别': '全日制教育',
                '日期': '2026.9~2030.7',
            }
        },
        {
            id: 8,
            state: 'un_submit',
            attrs: {
                '学历': '大学本科',
                '类别': '全日制教育',
                '日期': '2021.9~2024.7',
            }
        },
        {
            id: 9,
            state: 'un_submit',
            attrs: {
                '学历': '硕士研究生',
                '类别': '全日制教育',
                '日期': '2024.9~2026.7',
            }
        },
        {
            id: 10,
            state: 'un_submit',
            attrs: {
                '学历': '博士研究生',
                '类别': '全日制教育',
                '日期': '2026.9~2030.7',
            }
        },
        {
            id: 11,
            state: 'un_submit',
            attrs: {
                '学历': '博士研究生',
                '类别': '全日制教育',
                '日期': '2026.9~2030.7',
            }
        },
        {
            id: 12,
            state: 'un_submit',
            attrs: {
                '学历': '博士研究生',
                '类别': '全日制教育',
                '日期': '2026.9~2030.7',
            }
        },
        {
            id: 13,
            state: 'un_submit',
            attrs: {
                '学历': '博士研究生',
                '类别': '全日制教育',
                '日期': '2026.9~2030.7',
            }
        },
        {
            id: 14,
            state: 'un_submit',
            attrs: {
                '学历': '博士研究生',
                '类别': '全日制教育',
                '日期': '2026.9~2030.7',
            }
        },
    ] as InfoItem[];

    return (
        <>
            <Header>教育信息</Header>
            <InfoList
                onRefresh={async ()=>{
                    return {
                        data:{
                            total: data.length,
                            list: data
                        }
                    }
                }}
                onLoadMore={async ()=>{
                    return {
                        data:{
                            total: data.length,
                            list: data
                        }
                    }
                }}
                onDetailClick={(item)=>{
                    navigate('/person/education/form', {state: item})
                }}
            />
            <Footer>
                <Button
                    block={true}
                    color='primary'
                    size={'middle'}
                    style={{
                        margin:'15%'
                    }}
                >
                    新增教育信息
                </Button>
            </Footer>
        </>
    )
}

export default EducationIndex;
