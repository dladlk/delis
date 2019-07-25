import { Component, OnInit } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Range, RangeModel } from '../date-range/model/model';
import { ErrorService } from '../../../service/system/error.service';
import { TokenService } from '../../../service/system/token.service';
import { RuntimeConfigService } from '../../../service/system/runtime-config.service';
import { HttpRestService } from '../../../service/system/http-rest.service';
import { DaterangeObservable } from '../../../observable/daterange.observable';

@Component({
  selector: 'app-chart-document',
  templateUrl: './chart-document.component.html',
  styleUrls: ['./chart-document.component.scss']
})
export class ChartDocumentComponent implements OnInit {

  private readonly url: string;

  lineChartData: Array<any> = [];
  lineChartLabels: Array<any> = [];

  lineChartOptions: any;
  lineChartColors: Array<any>;
  lineChartLegend: boolean;
  lineChartType: string;

  drm: RangeModel;

  constructor(private errorService: ErrorService,
              private tokenService: TokenService,
              private configService: RuntimeConfigService,
              private httpRestService: HttpRestService,
              private daterangeObservable: DaterangeObservable) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/chart';
    this.updateLineChart(false);
    this.daterangeObservable.listen().subscribe((dtRange: Range) => {
      if (dtRange.fromDate !== null && dtRange.toDate !== null) {
        this.drm = dtRange;
        this.updateLineChart(true);
      } else {
        this.updateLineChart(false);
      }
    });
  }

  ngOnInit() {
    this.lineChartLegend = false;
    this.lineChartOptions = {
      responsive: true
    };
    this.lineChartType = 'line';
    this.lineChartColors = [
      {
        backgroundColor: 'rgba(33, 150, 243,0.2)',
        borderColor: 'rgba(33, 150, 243,1)',
        pointBackgroundColor: 'rgba(33, 150, 243,1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(77,83,96,1)'
      }
    ];
  }

  public chartClicked(e: any): void {
    if (e.active.length > 0) {
      console.log('Index', e.active[0]._index);
      console.log('Data', e.active[0]._chart.config.data.datasets[0].data[e.active[0]._index]);
      console.log('Label', e.active[0]._chart.config.data.labels[e.active[0]._index]);
    }
  }

  public chartHovered(e: any): void {
  }

  private updateLineChart(custom: boolean) {
    if (custom) {
      this.getChartCustomData(this.drm, false).subscribe(
        (data: {}) => {
          this.generateLineChart(data);
        }, error => {
          this.errorService.errorProcess(error);
        }
      );
    } else {
      this.drm = new RangeModel();
      var dateStart = new Date();
      dateStart.setHours(0, 0, 0, 0);
      var dateEnd = new Date();
      this.drm.fromDate = dateStart;
      this.drm.toDate = dateEnd;
      this.getChartDefaultData(dateStart, dateEnd, true).subscribe(
        (data: {}) => {
          this.generateLineChart(data);
        }, error => {
          this.errorService.errorProcess(error);
        }
      );
    }
  }

  generateLineChart(data: {}) {
    let lineChart = Object.assign({}, data['data']);
    this.lineChartData = lineChart.lineChartData;
    if (this.lineChartLabels.length !== 0) {
      this.lineChartLabels.length = 0;
      this.lineChartLabels.push(...lineChart.lineChartLabels);
    } else {
      this.lineChartLabels = lineChart.lineChartLabels;
    }
  }

  getChartCustomData(drm: Range, defaultChart: boolean): Observable<any> {
    let params = new HttpParams();
    if (this.drm.fromDate !== null && this.drm.toDate) {
      params = params.append('startDate', String(new Date(drm.fromDate).getTime()));
      params = params.append('endDate', String(new Date(drm.toDate).getTime()));
    }
    params = params.append('defaultChart', String(defaultChart));
    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
  }

  getChartDefaultData(start: Date, end: Date, defaultChart: boolean): Observable<any> {
    let params = new HttpParams();
    params = params.append('startDate', String(start.getTime()));
    params = params.append('endDate', String(end.getTime()));
    params = params.append('defaultChart', String(defaultChart));
    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
  }
}
