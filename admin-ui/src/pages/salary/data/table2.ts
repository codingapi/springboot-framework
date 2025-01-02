import {Table2, User} from "@/pages/salary/types";

export class Table2UpdateService {

    private data: Table2[];
    private readonly update: (data: Table2[]) => void;
    private readonly reload: () => void;
    private readonly updateVersion: (version: number) => void;

    constructor(table2: Table2[], update: (data: Table2[]) => void, reload: () => void, updateVersion: (version: number) => void) {
        this.data = table2;
        this.update = update;
        this.reload = reload;
        this.updateVersion = updateVersion;
    }


    public initData(users: User[]) {
        const data = users.map((user) => {
            return {
                id: user.id,
                name: user.name,
                kemu1: 0,
                kemu2: 0,
                sum: 0
            }
        });
        this.updateData(data);
    }


    public onChange(row: Table2) {
        const t1 = new Date().getMilliseconds();
        const sum = row.kemu1 + row.kemu2;
        const list = this.data.map((item) => {
            if (item.id === row.id) {
                return {
                    ...item,
                    ...row,
                    sum
                }
            }
            return item;
        });
        console.log(sum);
        this.updateData(list);
        const t2 = new Date().getMilliseconds();
        console.log("onChange", t2 - t1);
    }


    private updateData(data: Table2[]) {
        this.update(data);
        this.reload();
        this.updateVersion(Math.random());
    }
}
