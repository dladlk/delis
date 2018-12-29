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

    public chartDocumentModel: ChartDocumentModel;
    private period: number = 0;

    constructor(private translate: TranslateService, private locale: LocaleService, private chartDocumentTestGuiStaticService: ChartDocumentTestGuiStaticService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.chartDocumentModel = this.chartDocumentTestGuiStaticService.loadChartDocument(this.period);
    }

    ngOnInit() {}

    public chartClicked(e: any): void {}

    public chartHovered(e: any): void {}

    public nextPeriod() : void {
        this.period++;
        if (this.period > 2) {
            this.period = 2;
        }
        this.chartDocumentModel = this.chartDocumentTestGuiStaticService.loadChartDocument(this.period);
        this.chartDocumentModel.lineChartLabels.forEach(x => console.log('label = ' + x));
    }

    public previousPeriod() : void {
        this.period--;
        if (this.period < 0) {
            this.period = 0;
        }
        this.chartDocumentModel = this.chartDocumentTestGuiStaticService.loadChartDocument(this.period);
    }

    loadDate(e: any) {}
}
