export class ChartDocumentModel {

    public lineChartData: Array<any>;
    public lineChartLabels: Array<any>;
    public lineChartOptions: any;
    public lineChartColors: Array<any>;
    public lineChartLegend: boolean;
    public lineChartType: string;

    constructor() {
        this.lineChartLegend = true;
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
}