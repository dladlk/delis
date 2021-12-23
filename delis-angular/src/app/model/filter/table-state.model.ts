import { MatSort } from '@angular/material';
import { Range } from '../../component/system/date-range/model/model';
import { DetailsStateModel } from './details-state.model';

export class TableStateModel {

    dateRange: Range;
    sort: MatSort;
    pageIndex: number;
    pageSize: number;

    detailsState: DetailsStateModel;

    constructor(sort: MatSort) {
        this.sort = sort;
        this.dateRange = null;
        this.pageIndex = 0;
        this.pageSize = 10;
        this.detailsState = new DetailsStateModel();
    }
}
