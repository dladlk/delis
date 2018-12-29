import { Component, OnInit } from "@angular/core";
import { routerTransition } from "../../../../router.animations";
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../../../service/locale.service";
import {ChartDocumentModel} from "./model/chart.document.model";

@Component({
    selector: 'app-dashboard-chart-document',
    templateUrl: './chart.document.component.html',
    styleUrls: ['./chart.document.component.scss'],
    animations: [routerTransition()]
})
export class ChartDocumentComponent implements OnInit {

    chartDocumentModel: ChartDocumentModel;

    // events
    public chartClicked(e: any): void {
        console.log('chartClicked');
    }

    public chartHovered(e: any): void {
        console.log('chartHovered');
    }

    constructor(private translate: TranslateService, private locale: LocaleService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }

    ngOnInit() {
        this.initChartDocumentModel();
    }

    initChartDocumentModel() {
        this.chartDocumentModel = new ChartDocumentModel();
        this.chartDocumentModel.lineChartData = [
            { data: [18, 48, 77, 9, 87, 27, 40], label: 'Series C' }
        ];
        this.chartDocumentModel.lineChartLabels = [
            'January',
            'February',
            'March',
            'April',
            'May',
            'June',
            'July'
        ];
    }
}