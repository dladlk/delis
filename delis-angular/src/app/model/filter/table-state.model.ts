import { Range } from "../../component/system/date-range/model/model";
import {MatSort} from "@angular/material";

export class TableStateModel {

    dateRange: Range;
    sort: MatSort;
    pageIndex: number;
    pageSize: number;

    constructor(sort: MatSort) {
        this.sort = sort;
        this.dateRange = null;
        this.pageIndex = 0;
        this.pageSize = 10;
    }
}
