import { Component, OnInit } from "@angular/core";
import { routerTransition } from "../../../../router.animations";
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../../../service/locale.service";
import { ChartDocumentModel } from "./model/chart.document.model";
import { ChartDocumentTestGuiStaticService } from "./services/chart.document.test-gui-static.service";

@Component({
    selector: 'app-dashboard-chart-document',
    templateUrl: './chart.document.component.html',
    styleUrls: ['./chart.document.component.scss'],
    animations: [routerTransition()]
})
export class ChartDocumentComponent implements OnInit {

    chartDocumentModel: ChartDocumentModel;
    period: number = 0;

    // events
    public chartClicked(e: any): void {
        console.log('chartClicked');
    }

    public chartHovered(e: any): void {
        console.log('chartHovered');
    }

    constructor(private translate: TranslateService, private locale: LocaleService, private chartDocumentTestGuiStaticService: ChartDocumentTestGuiStaticService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }

    ngOnInit() {
        console.log('period in init = ' + this.period);
        if (this.period === 1) {
            this.chartDocumentModel = this.chartDocumentTestGuiStaticService.loadChartDocumentModelByYear();
        } else {
            this.chartDocumentModel = this.chartDocumentTestGuiStaticService.loadChartDocumentModelByMonth();
        }
    }

    nextPeriod() {
        this.period++;
        if (this.period === 1) {
            this.chartDocumentModel = null;
        } else {
            this.period = 1;
        }
        console.log('period = ' + this.period);
        this.ngOnInit();
    }

    previousPeriod() {
        this.period--;
        if (this.period === 0) {
            this.chartDocumentModel = null;
        } else {
            this.period = 0;
        }
        console.log('period = ' + this.period);
        this.ngOnInit();
    }

    loadDate() {}
}
