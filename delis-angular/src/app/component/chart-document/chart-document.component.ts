import { Component, OnInit } from '@angular/core';
import {DateRangePickerModel} from '../../model/system/date-range-picker.model';
import {TranslateService} from '@ngx-translate/core';
import {LocaleService} from '../../service/system/locale.service';
import {ErrorService} from '../../service/system/error.service';
import {DaterangeObservable} from '../../observable/daterange.observable';
import {Observable} from 'rxjs';
import {HttpParams} from '@angular/common/http';
import {HttpRestService} from '../../service/system/http-rest.service';
import {RuntimeConfigService} from '../../service/system/runtime-config.service';
import {TokenService} from '../../service/system/token.service';

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

  drm: DateRangePickerModel;

  constructor(
    private translate: TranslateService,
    private locale: LocaleService,
    private errorService: ErrorService,
    private tokenService: TokenService,
    private configService: RuntimeConfigService,
    private httpRestService: HttpRestService,
    private daterangeObservable: DaterangeObservable) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/chart';
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
    this.updateLineChart(false);
    this.daterangeObservable.listen().subscribe((dtRange: DateRangePickerModel) => {
      if (dtRange.startDate !== null && dtRange.endDate !== null) {
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
        backgroundColor: 'rgba(0,91,124,0.2)',
        borderColor: 'rgba(0,91,124,1)',
        pointBackgroundColor: 'rgba(0,91,124,1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(77,83,96,1)'
      }
    ];
  }

  public chartClicked(e: any): void {
    if(e.active.length > 0) {
      console.log("Index", e.active[0]._index);
      console.log("Data" , e.active[0]._chart.config.data.datasets[0].data[e.active[0]._index]);
      console.log("Label" , e.active[0]._chart.config.data.labels[e.active[0]._index]);
    }
  }

  public chartHovered(e: any): void {}

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
      this.drm = new DateRangePickerModel();
      var dateStart = new Date();
      dateStart.setHours(0,0,0,0);
      var dateEnd = new Date();
      this.drm.startDate = dateStart.toDateString();
      this.drm.endDate = dateEnd.toDateString();
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
    let lineChart = Object.assign({}, data["data"]);
    this.lineChartData = lineChart.lineChartData;
    if (this.lineChartLabels.length !== 0) {
      this.lineChartLabels.length = 0;
      this.lineChartLabels.push(... lineChart.lineChartLabels);
    } else {
      this.lineChartLabels = lineChart.lineChartLabels;
    }
  }

  getChartCustomData(drm: DateRangePickerModel, defaultChart: boolean): Observable<any> {
    let params = new HttpParams();
    if (this.drm.startDate !== null && this.drm.endDate) {
      params = params.append('startDate', String(new Date(drm.startDate).getTime()));
      params = params.append('endDate', String(new Date(drm.endDate).getTime()));
    }

    params = params.append('timeZone', Intl.DateTimeFormat().resolvedOptions().timeZone);
    params = params.append('defaultChart', String(defaultChart));
    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
  }

  getChartDefaultData(start: Date, end: Date, defaultChart: boolean): Observable<any> {
    let params = new HttpParams();
    params = params.append('startDate', String(start.getTime()));
    params = params.append('endDate', String(end.getTime()));
    params = params.append('timeZone', Intl.DateTimeFormat().resolvedOptions().timeZone);
    params = params.append('defaultChart', String(defaultChart));
    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
  }
}
