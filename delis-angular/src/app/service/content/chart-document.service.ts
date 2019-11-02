import { Injectable } from '@angular/core';
import { Range } from '../../component/system/date-range/model/model';

@Injectable({
  providedIn: 'root'
})
export class ChartDocumentService {

  get chartType(): string {
    return this._chartType;
  }

  get range(): Range {
    return this._range;
  }

  private _range: Range;
  private _chartType: string;

  updateRange(value: Range) {
    this._range = value;
  }

  updateChartType(chartType: string) {
    this._chartType = chartType;
  }

  resetRange() {
    this._range = undefined;
  }

  resetChartType() {
    this._chartType = undefined;
  }
}
