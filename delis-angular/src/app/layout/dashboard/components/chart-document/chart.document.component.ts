import { Component, OnInit } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from "../../../../router.animations";
import { LocaleService } from "../../../../service/locale.service";
import { ChartDocumentService } from "./services/chart.document.service";
import { ErrorService } from "../../../../service/error.service";
import { DaterangeService } from "../../../bs-component/components/daterange/daterange.service";
import { DateRangeModel } from "../../../../models/date.range.model";
import { DaterangeShowService } from "../../../bs-component/components/daterange/daterange.show.service";

@Component({
    selector: 'app-dashboard-chart-document',
    templateUrl: './chart.document.component.html',
    styleUrls: ['./chart.document.component.scss'],
    animations: [routerTransition()]
})
export class ChartDocumentComponent implements OnInit {

    lineChartData: Array<any> = [];
    lineChartLabels: Array<any> = [];

    lineChartOptions: any;
    lineChartColors: Array<any>;
    lineChartLegend: boolean;
    lineChartType: string;

    startDate: Date;

    drm: DateRangeModel = new DateRangeModel();

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private errorService: ErrorService,
        private dtService: DaterangeService,
        private dtShowService: DaterangeShowService,
        private chartDocumentService: ChartDocumentService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.updateLineChart(false);
        this.dtService.listen().subscribe((dtRange: DateRangeModel) => {
            if (dtRange.dateStart !== null && dtRange.dateEnd !== null) {
                this.drm = dtRange;
                this.updateLineChart(true);
            } else {
                this.updateLineChart(false);
            }
        });
        this.dtShowService.listen().subscribe((show: boolean) => {
            this.updateLineChart(false);
        });
    }

    ngOnInit() {
        this.startDate = new Date();
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
            this.chartDocumentService.getChartCustomData(this.drm).subscribe(
                (data: {}) => {
                    this.generateLineChart(data);
                }, error => {
                    this.errorService.errorProcess(error);
                }
            );
        } else {
            this.drm = new DateRangeModel();
            this.drm.dateStart = new Date();
            this.drm.dateStart.setHours(0,0,0,0);
            this.drm.dateEnd = new Date();
            this.chartDocumentService.getChartCustomData(this.drm).subscribe(
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
