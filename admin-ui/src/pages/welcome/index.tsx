import React, {useCallback, useMemo, useState} from 'react';
import './index.scss';
import Page from "@/components/Layout/Page";

import {AgGridReact} from 'ag-grid-react';
import {AllCommunityModule, ModuleRegistry, themeQuartz,} from "ag-grid-community";
import {AG_GRID_LOCALE_CN} from '@ag-grid-community/locale';

const localeText = AG_GRID_LOCALE_CN;
// Register all Community features
ModuleRegistry.registerModules([AllCommunityModule]);


const myTheme = themeQuartz.withParams({
    /* Low spacing = very compact */
    spacing: 2,
    accentColor: "red",

});
const Index = () => {
    const [rowData, setRowData] = useState();
    const [columnDefs, setColumnDefs] = useState([
        {
            field: "athlete",
            minWidth: 170,
            headerName: "运动员"
        },
        {
            field: "age",
            headerName: "年龄"
        },
        {
            field: "country",
            headerName: '国家信息',
            headerClass: 'country-header',
            children: [
                {
                    field: "country",
                    headerName: "国家"
                },
                {field: "name"},
                {field: "code"}
            ]
        },
        {field: "year", headerName: "年份"},
        {field: "date", headerName: "日期"},
        {field: "sport", headerName: "运动"},
        {field: "gold", headerName: "金牌"},
        {field: "silver", headerName: "银牌"},
        {field: "bronze", headerName: "铜牌"},
        {field: "total", headerName: "总计",editable: false},
    ]);
    const theme = useMemo(() => {
        return myTheme;
    }, []);
    const defaultColDef = useMemo(() => {
        return {
            editable: true,
            filter: true,
        };
    }, []);

    const onGridReady = useCallback((params: any) => {
        fetch("https://www.ag-grid.com/example-assets/olympic-winners.json")
            .then((resp) => resp.json())
            .then((data) => setRowData(data));
    }, []);


    return (
        <Page enablePageContainer={true}>
            <div
                // define a height because the Data Grid will fill the size of the parent container
                style={{height: 500}}
            >
                <AgGridReact
                    rowData={rowData}
                    localeText={localeText}
                    onRowDataUpdated={(params) => {
                        console.log("Row Data Updated", params)
                    }}
                    onCellValueChanged={(event) => {
                        console.log(`New Cell Value:`, event);
                        // 当金牌，银牌，铜牌的值发生变化时，更新总计

                        //@ts-ignore
                        if (['gold', 'silver', 'bronze'].includes(event.colDef.field)) {
                            const gold = event.data.gold;
                            const silver = event.data.silver;
                            const bronze = event.data.bronze;
                            const total = gold + silver + bronze;
                            event.data.total = total;
                            event.api.refreshCells({columns: ['total']});
                        }
                    }}

                    columnDefs={columnDefs}
                    theme={theme}
                    pagination={true}
                    defaultColDef={defaultColDef}
                    onGridReady={onGridReady}

                />
            </div>
        </Page>
    );
}

export default Index;
