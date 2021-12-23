import { Injectable } from '@angular/core';
import { Range } from '../../component/system/date-range/model/model';

@Injectable({
  providedIn: 'root'
})
export class ChartDocumentService {

  get chartType(): string {
    return this.localChartType;
  }

  get range(): Range {
    return this.localRange;
  }

  private localRange: Range;
  private localChartType: string;

  updateRange(value: Range) {
    this.localRange = value;
  }

  updateChartType(chartType: string) {
    this.localChartType = chartType;
  }

  resetRange() {
    this.localRange = undefined;
  }

  resetChartType() {
    this.localChartType = undefined;
  }
}
