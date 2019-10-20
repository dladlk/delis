import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable, Subscription } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import * as moment from 'moment';

import { Range } from '../date-range/model/model';
import { DashboardModel } from '../../../model/content/dashboard.model';
import { ErrorService } from '../../../service/system/error.service';
import { TokenService } from '../../../service/system/token.service';
import { RuntimeConfigService } from '../../../service/system/runtime-config.service';
import { HttpRestService } from '../../../service/system/http-rest.service';
import { SpinnerService } from '../../../service/system/spinner.service';
import { DaterangeObservable } from '../../../observable/daterange.observable';
import { ChartDocumentService } from '../../../service/content/chart-document.service';
import { RedirectContentService } from '../../../service/content/redirect-content.service';
import { RefreshObservable } from '../../../observable/refresh.observable';
import { ResetDaterangeForTodayObservable } from '../../../observable/reset-daterange-for-today.observable';
import { DashboardObservable } from '../../../observable/dashboard.observable';

const LINE_CHART_TYPE = 'line';
const BAR_CHART_TYPE = 'bar';

import {
  DOCUMENT_PATH,
  SEND_DOCUMENT_PATH,
  CHART_DATE_FORMAT,
  CHART_DATE_FORMAT_START,
  CHART_DATE_FORMAT_END
} from '../../../app.constants';

@Component({
  selector: 'app-chart-document',
  templateUrl: './chart-document.component.html',
  styleUrls: ['./chart-document.component.scss'],
  providers: [
    DatePipe
  ]
})
export class ChartDocumentComponent implements OnInit, OnDestroy {

  private readonly url: string;

  showing = false;

  deliveryAlertCount = 0;

  lineChartData: Array<any> = [];
  lineChartLabels: Array<string> = [];

  lineChartOptions: any;
  barChartOptions: any;
  lineChartColors: Array<any>;
  barChartColors: Array<any>;
  lineChartLegend: boolean;

  private rangeUpdate$: Subscription;
  range: Range;

  isLineChart: boolean;
  isBarChart: boolean;

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
              private dashboardObservable: DashboardObservable,
              public spinnerService: SpinnerService) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/chart';
    this.rangeUpdate$ = this.daterangeObservable.listen().subscribe((dtRange: Range) => {
      this.updateLineChart(dtRange);
    });
  }

  ngOnInit() {
    this.initRange();
    this.initChartType();
    this.updateLineChart(this.range);
    this.lineChartLegend = true;
    this.lineChartOptions = {
      scaleShowVerticalLines: false,
      responsive: true,
      showScale: false,
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
    this.barChartOptions = {
      scaleShowVerticalLines: false,
      responsive: true,
      showScale: false,
      scales: {
        yAxes: [{
          ticks: {
            stepSize: 1
          }
        }]
      }
    };
    // recommended https://color.adobe.com/ru/create
    this.barChartColors = [
      {
        // error document color
        backgroundColor: 'rgba(255, 46, 38)',
      },
      {
        // document color
        backgroundColor: 'rgba(13, 146, 255)',
      },
      {
        // send document color
        backgroundColor: 'rgba(251, 140, 0)',
      }
    ];
    this.lineChartColors = [
      {
        // error document color
        borderColor: 'rgba(255, 46, 38, 1)',
        pointBackgroundColor: 'rgba(255, 46, 38, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(255, 46, 38, 0.8)',
        backgroundColor: 'rgba(255, 46, 38, 0.2)',
      },
      {
        // document color
        borderColor: 'rgba(13, 146, 255, 1)',
        pointBackgroundColor: 'rgba(13, 146, 255, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(13, 146, 255, 0.8)',
        backgroundColor: 'rgba(13, 146, 255, 0.2)',
      },
      {
        // send document color
        borderColor: 'rgba(251, 140, 0, 1)',
        pointBackgroundColor: 'rgba(251, 140, 0, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(251, 140, 0, 0.8)',
        backgroundColor: 'rgba(251, 140, 0, 0.2)',
      }
    ];
  }

  ngOnDestroy() {
    if (this.rangeUpdate$) {
      this.rangeUpdate$.unsubscribe();
    }
  }

  checkLineChart() {
    this.isLineChart = false;
    this.isBarChart = true;
    this.chartDocumentService.updateChartType(BAR_CHART_TYPE);
  }

  checkBarChart() {
    this.isLineChart = true;
    this.isBarChart = false;
    this.chartDocumentService.updateChartType(LINE_CHART_TYPE);
  }

  initRange() {
    this.range = this.chartDocumentService.range;
    if (!this.range) {
      const today = new Date();
      this.range = {fromDate: today, toDate: today};
    }
  }

  initChartType() {
    const chartType = this.chartDocumentService.chartType;
    if (chartType) {
      if (chartType === LINE_CHART_TYPE) {
        this.checkBarChart();
      } else {
        this.checkLineChart();
      }
    } else {
      this.checkBarChart();
    }
  }

  public chartClicked(e: any): void {
    if (e.active.length > 0) {
      const activePoint = e.active[0]._chart.getElementAtEvent(e.event)[0];
      const datasetIndex = activePoint._datasetIndex; // current line
      const labelIndex = activePoint._index; // current label
      const label = this.lineChartLabels[labelIndex];
      const path = datasetIndex > 1 ? SEND_DOCUMENT_PATH : DOCUMENT_PATH;
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
        if (labelIndex + 1 === this.lineChartLabels.length) {
          start = moment(this.range.fromDate).format(CHART_DATE_FORMAT + ' ' + label + ':00');
          end = moment(new Date()).format(CHART_DATE_FORMAT_END);
        } else {
          start = moment(this.range.fromDate).format(CHART_DATE_FORMAT + ' ' + label + ':00');
          end = moment(this.range.toDate).format(CHART_DATE_FORMAT + ' ' + this.lineChartLabels[labelIndex + 1] + ':00');
        }
      } else {
        const date = label.split('.');
        const day = date[0];
        const month = date[1];
        const year = date[2];
        start = moment(year + '-' + month + '-' + day).format(CHART_DATE_FORMAT_START);
        end = moment(year + '-' + month + '-' + day).format(CHART_DATE_FORMAT_END);
      }

      const data = {
        dateStart: start,
        dateEnd: end,
        statusError,
        path
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
        res = this.datePipe.transform(d, 'yyyy-MM-dd');
      }
      return res;
    };
    const dateStart = formatDate(range.fromDate);
    const dateEnd = formatDate(range.toDate);
    this.getChartData(dateStart, dateEnd).subscribe(
        (data: {}) => {
          this.generateLineChart(data);
          this.generateDashboardData();
        }, error => {
          this.errorService.errorProcess(error);
        }
    );
  }

  private generateLineChart(data: any) {
    const lineChart = Object.assign({}, data.data);
    this.deliveryAlertCount = lineChart.deliveryAlertCount;
    this.lineChartData = lineChart.lineChartData;
    let totalSum = 0;
    this.lineChartData.forEach(d => {
      this.translate.get('dashboard.' + d.label).subscribe(s => {
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
    const params = new HttpParams()
        .append('from', start)
        .append('to', end)
        .append('now', this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss'));
    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
  }

  generateDashboardData() {
    const dashboardModel = new DashboardModel();
    dashboardModel.deliveryAlertCount = this.deliveryAlertCount;
    for (const line of this.lineChartData) {
      const label = line.label;
      const arr = line.data;
      if (label === 'Received error' || label === 'Modtaget med fejl') {
        dashboardModel.errorLastHour = arr.length !== 0 ? arr.reduce((a, b) => a + b, 0) : 0;
      }
      if (label === 'Received' || label === 'Modtaget') {
        dashboardModel.receivedDocumentsLastHour = arr.length !== 0 ? arr.reduce((a, b) => a + b, 0) : 0;
      }
      if (label === 'Sent' || label === 'Afsendt') {
        dashboardModel.sendDocumentsLastHour = arr.length !== 0 ? arr.reduce((a, b) => a + b, 0) : 0;
      }
    }
    const range = this.chartDocumentService.range;
    let start;
    let end;
    if (range.fromDate !== null && range.toDate !== null) {
      start = moment(range.fromDate).format(CHART_DATE_FORMAT);
      end = moment(range.toDate).format(CHART_DATE_FORMAT);
      range.fromDate = new Date(start);
      range.toDate = new Date(end);
    } else {
      start = null;
      end = null;
    }
    const data = {
      dashboardModel,
      dateStart: start,
      dateEnd: end
    };
    this.dashboardObservable.setDashboard(data);
  }

  refresh() {
    this.refreshObservable.refreshPage();
    this.updateLineChart(this.range);
  }

  clear() {
    this.chartDocumentService.resetRange();
    this.chartDocumentService.resetChartType();
    this.resetDaterangeForTodayObservable.resetForToday();
    this.initRange();
    this.refresh();
  }
}
