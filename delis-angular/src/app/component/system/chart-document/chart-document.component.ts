import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable, Subscription } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { Router } from "@angular/router";
import * as moment from 'moment';

import { Range, RangeModel } from '../date-range/model/model';
import { ErrorService } from '../../../service/system/error.service';
import { TokenService } from '../../../service/system/token.service';
import { RuntimeConfigService } from '../../../service/system/runtime-config.service';
import { HttpRestService } from '../../../service/system/http-rest.service';
import { DaterangeObservable } from '../../../observable/daterange.observable';
import { ChartDocumentService } from "../../../service/content/chart-document.service";
import { RedirectContentService } from "../../../service/content/redirect-content.service";
import { RefreshObservable } from "../../../observable/refresh.observable";
import { ResetDaterangeForTodayObservable } from "../../../observable/reset-daterange-for-today.observable";
import { DashboardObservable } from "../../../observable/dashboard.observable";
import { DashboardModel } from "../../../model/content/dashboard.model";

import { DOCUMENT_PATH, SEND_DOCUMENT_PATH, CHART_DATE_FORMAT } from '../../../app.constants';

@Component({
  selector: 'app-chart-document',
  templateUrl: './chart-document.component.html',
  styleUrls: ['./chart-document.component.scss'],
  providers:[
    DatePipe
  ]
})
export class ChartDocumentComponent implements OnInit, OnDestroy {

  private readonly url: string;

  showing = false;

  lineChartData: Array<any> = [];
  lineChartLabels: Array<string> = [];

  lineChartOptions: any;
  lineChartColors: Array<any>;
  lineChartLegend: boolean;
  lineChartType: any;

  drm: RangeModel;
  private rangeUpdate$: Subscription;
  range: Range;

  constructor(private router: Router,
              private errorService: ErrorService,
              private tokenService: TokenService,
              private configService: RuntimeConfigService,
              private httpRestService: HttpRestService,
              private translate: TranslateService,
              private chartDocumentService: ChartDocumentService,
              private redirectService: RedirectContentService,
              private datePipe: DatePipe,
              private daterangeObservable: DaterangeObservable,
              private refreshObservable: RefreshObservable,
              private resetDaterangeForTodayObservable: ResetDaterangeForTodayObservable,
              private dashboardObservable: DashboardObservable) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/chart';
    this.rangeUpdate$ = this.daterangeObservable.listen().subscribe((dtRange: Range) => {
      this.updateLineChart(dtRange);
    });
  }

  ngOnInit() {
    this.initRange();
    this.updateLineChart(this.range);
    this.lineChartLegend = true;
    this.lineChartType = 'line';
    this.lineChartOptions = {
      responsive: true,
      scales: {
        yAxes: [{
          ticks: {
            stepSize: 1
          }
        }]
      },
      elements: {
            point: {
                  radius: 6,
                  hitRadius: 5,
                  hoverRadius: 10,
                  hoverBorderWidth: 3
                }
          }
    };
    const backgroundColor = 'rgba(11, 120, 208, 0.2)';

    // recommended https://color.adobe.com/ru/create
    this.lineChartColors = [
      {
        // error document color
        borderColor: 'rgba(255, 46, 38, 1)',
        pointBackgroundColor: 'rgba(255, 46, 38, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(255, 46, 38, 0.8)',
        backgroundColor: backgroundColor,
      },
      {
        // document color
        borderColor: 'rgba(13, 146, 255, 1)',
        pointBackgroundColor: 'rgba(13, 146, 255, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(13, 146, 255, 0.8)',
        backgroundColor: backgroundColor,
      },
      {
        // send document color
        borderColor: 'rgba(251, 140, 0, 1)',
        pointBackgroundColor: 'rgba(251, 140, 0, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(251, 140, 0, 0.8)',
        backgroundColor: backgroundColor,
      }
    ];
  }

  ngOnDestroy() {
    if (this.rangeUpdate$) {
      this.rangeUpdate$.unsubscribe();
    }
  }

  initRange() {
    this.range = this.chartDocumentService.range;
    if (!this.range) {
      let today = new Date();
      this.range = {fromDate: today, toDate: today};
    }
  }

  refresh() {
    this.refreshObservable.refreshPage();
    this.updateLineChart(this.range);
  }

  clear() {
    this.chartDocumentService.resetRange();
    this.resetDaterangeForTodayObservable.resetForToday();
    this.initRange();
    this.refresh();
  }

  public chartClicked(e: any): void {
    if (e.active.length > 0) {
      const activePoint = e.active[0]._chart.getElementAtEvent(e.event)[0];
      const datasetIndex = activePoint._datasetIndex; // current line
      const labelIndex = activePoint._index; // current label
      const label = this.lineChartLabels[labelIndex];
      let path = datasetIndex > 1 ? SEND_DOCUMENT_PATH : DOCUMENT_PATH;
      let statusError;
      switch (datasetIndex) {
        case 0 : {
          statusError = true;
          break;
        }
        case 1 : {
          statusError = false;
          break;
        }
        default : {
          statusError = undefined;
        }
      }
      let start;
      let end;
      if (label.indexOf(':') >= 0) {
        start = moment(this.range.fromDate).format(CHART_DATE_FORMAT);
        end = moment(this.range.toDate).format(CHART_DATE_FORMAT);
      } else {
        let year = this.range.fromDate.getFullYear();
        let date = label.split('.');
        let day = date[0];
        let month = date[1];
        start = moment(year + "-" + month + "-" + day).format(CHART_DATE_FORMAT);
        end = moment(year + "-" + month + "-" + day).format(CHART_DATE_FORMAT);
      }

      let data = {
        dateStart: start,
        dateEnd: end,
        statusError: statusError,
        path: path
      };
      this.redirectService.updateRedirectData(data);
      this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
          this.router.navigate(['/' + path]));
    }
  }

  public chartHovered(e: any): void { }

  private updateLineChart(range: Range) {
    this.chartDocumentService.updateRange(range);
    this.range = range;
    const formatDate = (d: Date) => {
      let res = '';
      if (d) {
        res = this.datePipe.transform(d, "yyyy-MM-dd");
      }
      return res;
    };
    var dateStart = formatDate(range.fromDate);
    var dateEnd = formatDate(range.toDate);
    this.getChartData(dateStart, dateEnd).subscribe(
        (data: {}) => {
          this.generateLineChart(data);
          this.generateDashboardData();
        }, error => {
          this.errorService.errorProcess(error);
        }
    );
  }

  private generateLineChart(data: {}) {
    let lineChart = Object.assign({}, data['data']);
    this.lineChartData = lineChart.lineChartData;
    let totalSum = 0;
    this.lineChartData.forEach(d => {
      this.translate.get("dashboard."+d.label).subscribe(s => {
        d.label = s;
      });
      if (d.data.length > 0 && d.data.reduce((sum, current) => sum + current) > 0) {
        ++totalSum;
      }
    });
    this.showing = totalSum !== 0;
    if (this.lineChartLabels.length !== 0) {
      this.lineChartLabels.length = 0;
      this.lineChartLabels.push(...lineChart.lineChartLabels);
    } else {
      this.lineChartLabels = lineChart.lineChartLabels;
    }
  }

  private getChartData(start: string, end: string): Observable<any> {
    let params = new HttpParams()
        .append('from', start)
        .append('to', end)
        .append('now', this.datePipe.transform(new Date(), "yyyy-MM-dd HH:mm:ss"));
    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
  }

  generateDashboardData() {
    let dashboardModel = new DashboardModel();
    for (const data of this.lineChartData) {
      let label = data.label;
      let arr = data.data;
      if (label === 'Received error' || label === 'Modtaget med fejl') {
        dashboardModel.errorLastHour = arr.length !== 0 ? arr.reduce((a,b) => a + b, 0) : 0;
      }
      if (label === 'Received' || label === 'Modtaget') {
        dashboardModel.receivedDocumentsLastHour = arr.length !== 0 ? arr.reduce((a,b) => a + b, 0) : 0;
      }
      if (label === 'Sent' || label === 'Afsendt') {
        dashboardModel.sendDocumentsLastHour = arr.length !== 0 ? arr.reduce((a,b) => a + b, 0) : 0;
      }
    }

    let range = this.chartDocumentService.range;
    let start;
    let end;
    if (range.fromDate !== null && range.toDate !== null) {
      start = moment(range.fromDate).format(CHART_DATE_FORMAT);
      end = moment(range.toDate).format(CHART_DATE_FORMAT);
      range.fromDate = new Date(start);
      range.toDate = new Date(end);
    } else {
      start = moment(range.fromDate).format(CHART_DATE_FORMAT);
      end = moment(range.toDate).format(CHART_DATE_FORMAT);
    }

    let data = {
      dashboardModel: dashboardModel,
      dateStart: start,
      dateEnd: end
    };

    this.dashboardObservable.setDashboard(data);
  }
}
