import { Component, OnInit } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from "../../../../router.animations";
import { LocaleService } from "../../../../service/locale.service";
import { ChartDocumentService } from "./services/chart.document.service";
import { ErrorService } from "../../../../service/error.service";

@Component({
    selector: 'app-dashboard-chart-document',
    templateUrl: './chart.document.component.html',
    styleUrls: ['./chart.document.component.scss'],
    animations: [routerTransition()]
})
export class ChartDocumentComponent implements OnInit {

    private period: number = 0;

    lineChartData: Array<any> = [];
    lineChartLabels: Array<any> = [];

    lineChartOptions: any;
    lineChartColors: Array<any>;
    lineChartLegend: boolean;
    lineChartType: string;

    customDate: Date[];

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private errorService: ErrorService,
        private chartDocumentService: ChartDocumentService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.updateLineChart(false);
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

    nextPeriod() {
        this.period++;
        if (this.period > 2) {
            this.period = 2;
        }
        this.updateLineChart(false);
    }

    previousPeriod() {
        this.period--;
        if (this.period < 0) {
            this.period = 0;
        }
        this.updateLineChart(false);
    }

    loadDate(date: Date[]) {
        this.customDate = date;
        this.updateLineChart(true);
    }

    private updateLineChart(custom: boolean) {
        if (custom) {
            this.chartDocumentService.getChartCustomData(this.customDate).subscribe(
                (data: {}) => {
                    this.generateLineChart(data);
                }, error => {
                    this.errorService.errorProcess(error);
                }
            );
        } else {
            this.chartDocumentService.getChartData().subscribe(
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
        this.lineChartData=lineChart.lineChartData;
        if (this.lineChartLabels.length !== 0) {
            this.lineChartLabels.length = 0;
            this.lineChartLabels.push(... lineChart.lineChartLabels);
        } else {
            this.lineChartLabels=lineChart.lineChartLabels;
        }
    }
}
