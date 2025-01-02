import {Table1, Table2, User} from "@/pages/salary/types";
import {SalaryStore} from "@/pages/salary/store/salary";

export class Table1UpdateService {

    private readonly data: Table1[];
    private readonly update: (data: Table1[]) => void;
    private readonly reload: () => void;


    constructor(store: SalaryStore, update: (data: Table1[]) => void, reload: () => void) {
        this.data = store.table1;
        this.update = update;
        this.reload = reload;

    }


    public initData(users: User[]) {
        const t1 = new Date().getTime();
        const data = users.map((user) => {
            return {
                id: user.id,
                name: user.name,
                jxgz: 0,
                sum: 0
            }
        });
        this.updateData(data);
        const t2 = new Date().getTime();
        console.log("initData", t2 - t1);
    }


    public reloadData(table2: Table2[]) {
        const list = this.data.map((item) => {
            const row = table2.find((row) => row.id === item.id);
            if (row) {
                if (item.id === row.id) {
                    return {
                        ...item,
                        jxgz: row.sum,
                        sum: row.sum
                    }
                }
            }
            return item;
        });
        this.updateData(list);
    }


    private updateData(data: Table1[]) {
        this.update(data);
        this.reload();
    }

}
