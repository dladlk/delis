import { Component, OnInit } from "@angular/core";
import { routerTransition } from "../../../../router.animations";
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../../../service/locale.service";

@Component({
    selector: 'app-dashboard-chart-document',
    templateUrl: './chart.document.component.html',
    styleUrls: ['./chart.document.component.scss'],
    animations: [routerTransition()]
})
export class ChartDocumentComponent implements OnInit {

    public lineChartData: Array<any> = [
        { data: [18, 48, 77, 9, 87, 27, 40], label: 'Series C' }
    ];

    public lineChartLabels: Array<any> = [
        'January',
        'February',
        'March',
        'April',
        'May',
        'June',
        'July'
    ];
    public lineChartOptions: any = {
        responsive: true
    };

    public lineChartColors: Array<any> = [
        {
            backgroundColor: 'rgba(0,91,124,0.2)',
            borderColor: 'rgba(0,91,124,1)',
            pointBackgroundColor: 'rgba(0,91,124,1)',
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(77,83,96,1)'
        }
    ];

    public lineChartLegend: boolean;
    public lineChartType: string;

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
        this.lineChartLegend = true;
        this.lineChartType = 'line';
    }
}