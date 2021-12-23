import { MatSort } from '@angular/material/sort';
import {Range} from '../../component/system/date-range/model/model';
import {DetailsStateModel} from './details-state.model';
import {HttpParams} from '@angular/common/http';

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

  public toHttpParams(): HttpParams {
    let params = new HttpParams();
    params = params.append('page', String(this.pageIndex));
    params = params.append('size', String(this.pageSize));
    params = params.append('sort', this.sort.active);
    return params.append('order', this.sort.direction);
  }
}
