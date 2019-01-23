import { Component, OnInit } from "@angular/core";
import { routerTransition } from "../../../../router.animations";
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../../../service/locale.service";
import { ChartDocumentTestGuiStaticService } from "./services/chart.document.test-gui-static.service";

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

    constructor(private translate: TranslateService, private locale: LocaleService, private chartDocumentTestGuiStaticService: ChartDocumentTestGuiStaticService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.updateLineChart();
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
        this.updateLineChart();
    }

    previousPeriod() {
        this.period--;
        if (this.period < 0) {
            this.period = 0;
        }
        this.updateLineChart();
    }

    loadDate(e: any) {}

    private updateLineChart() {
        let clone = JSON.parse(JSON.stringify(this.chartDocumentTestGuiStaticService.loadChartDocument(this.period)));
        this.lineChartData=clone.lineChartData;
        if (this.lineChartLabels.length !== 0) {
            this.lineChartLabels.length = 0;
            this.lineChartLabels.push(...clone.lineChartLabels);
        } else {
            this.lineChartLabels=clone.lineChartLabels;
        }
    }
}
