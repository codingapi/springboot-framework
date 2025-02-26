import React from 'react'
import * as icons from '@ant-design/icons'
import {ProFormSelect, ProFormSelectProps} from '@ant-design/pro-components'
import Index from "@/components/View/MenuIcon";

export interface ProFormIconsProps extends ProFormSelectProps {
    placeholder?: string
}

const ProFormIcons: React.FC<ProFormIconsProps> = (props) => {
    //@ts-ignore
    const iconList = Object.keys(icons).filter((item: any) => typeof icons[item] === 'object')
    const options = iconList.map((item: any) => {
        return {
            label: (
                <>
                    <Index icon={item} style={{marginLeft: '8px'}}/> {item}
                </>
            ),
            value: item
        }
    });

    return (
        <ProFormSelect
            {
                ...props
            }
            showSearch
            allowClear
            options={options}
        >
        </ProFormSelect>
    )
}

export default ProFormIcons;
