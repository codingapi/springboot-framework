import React from "react";
import Header from "@/layout/Header";
import InfoList, {InfoItem} from "@/components/info/List";
import FooterButton from "@/components/info/Footer";
import {useNavigate} from "react-router";

const EducationIndex = () => {

    const list = [
        {
            id: 1,
            state: 'un_submit',
            '学历': '大学本科',
            '类别': '全日制教育',
            '日期': '2021.9~2024.7',
        },
        {
            id: 2,
            state: 'un_submit',
            '学历': '硕士研究生',
            '类别': '全日制教育',
            '日期': '2024.9~2026.7',
        },
        {
            id: 3,
            state: 'un_submit',
            '学历': '博士研究生',
            '类别': '全日制教育',
            '日期': '2026.9~2030.7',
        },
        {
            id: 4,
            state: 'un_submit',
            '学历': '博士研究生',
            '类别': '全日制教育',
            '日期': '2026.9~2030.7',
        },
        {
            id: 5,
            state: 'un_submit',
            '学历': '博士研究生',
            '类别': '全日制教育',
            '日期': '2026.9~2030.7',
        },
        {
            id: 6,
            state: 'un_submit',
            '学历': '博士研究生',
            '类别': '全日制教育',
            '日期': '2026.9~2030.7',
        },
        {
            id: 7,
            state: 'un_submit',
            '学历': '博士研究生',
            '类别': '全日制教育',
            '日期': '2026.9~2030.7',
        },

        {
            id: 1,
            state: 'un_submit',
            '学历': '大学本科',
            '类别': '全日制教育',
            '日期': '2021.9~2024.7',
        },
        {
            id: 2,
            state: 'un_submit',
            '学历': '硕士研究生',
            '类别': '全日制教育',
            '日期': '2024.9~2026.7',
        },
        {
            id: 3,
            state: 'un_submit',
            '学历': '博士研究生',
            '类别': '全日制教育',
            '日期': '2026.9~2030.7',
        },
        {
            id: 4,
            state: 'un_submit',
            '学历': '博士研究生',
            '类别': '全日制教育',
            '日期': '2026.9~2030.7',
        },
        {
            id: 5,
            state: 'un_submit',
            '学历': '博士研究生',
            '类别': '全日制教育',
            '日期': '2026.9~2030.7',
        },
        {
            id: 6,
            state: 'un_submit',
            '学历': '博士研究生',
            '类别': '全日制教育',
            '日期': '2026.9~2030.7',
        },
        {
            id: 7,
            state: 'un_submit',
            '学历': '博士研究生',
            '类别': '全日制教育',
            '日期': '2026.9~2030.7',
        },
    ] as InfoItem[];

    const navigate = useNavigate();

    return (
        <>
            <Header>教育信息</Header>
            <InfoList
                list={list}
                onDetailClick={(item)=>{
                    navigate('/person/education/form', {state: item})
                }}
            />
            <FooterButton text={'新增教育信息'}/>
        </>
    )
}

export default EducationIndex;
