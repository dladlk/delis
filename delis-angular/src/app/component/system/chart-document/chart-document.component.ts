import { Component, OnInit } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { DASHBOARD_PATH } from '../../../app.constants';

import { Range, RangeModel } from '../date-range/model/model';
import { ErrorService } from '../../../service/system/error.service';
import { TokenService } from '../../../service/system/token.service';
import { RuntimeConfigService } from '../../../service/system/runtime-config.service';
import { HttpRestService } from '../../../service/system/http-rest.service';
import { DaterangeObservable } from '../../../observable/daterange.observable';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-chart-document',
  templateUrl: './chart-document.component.html',
  styleUrls: ['./chart-document.component.scss'],
  providers:[
    DatePipe
  ]
})
export class ChartDocumentComponent implements OnInit {

  private readonly url: string;

  lineChartData: Array<any> = [];
  lineChartLabels: Array<string> = [];

  lineChartOptions: any;
  lineChartColors: Array<any>;
  lineChartLegend: boolean;
  lineChartType: string;

  drm: RangeModel;

  constructor(private errorService: ErrorService,
              private tokenService: TokenService,
              private configService: RuntimeConfigService,
              private httpRestService: HttpRestService,
              private translate: TranslateService,
              private datePipe: DatePipe,
              private daterangeObservable: DaterangeObservable) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/chart';

    let today = new Date();
    let range: Range = {fromDate: today, toDate: today};
    this.updateLineChart(range);
    this.daterangeObservable.listen().subscribe((dtRange: Range) => {
      if (location.href.endsWith('/' + DASHBOARD_PATH)) {
        this.updateLineChart(dtRange);
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

  private updateLineChart(range: Range) {
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
        }, error => {
          this.errorService.errorProcess(error);
        }
      );
  }

  private generateLineChart(data: {}) {
    let lineChart = Object.assign({}, data['data']);
    this.lineChartData = lineChart.lineChartData;
    this.lineChartData.forEach(d => {
      this.translate.get("dashboard."+d.label).subscribe(s => {
        d.label = s; 
      });
    });
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
}
