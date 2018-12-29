import { Injectable } from "@angular/core";
import { ChartDocumentModel } from "../model/chart.document.model";
import * as data_year from '../json/chart_document_year.json';
import * as data_month from '../json/chart_document_month.json';
import * as data_week from '../json/chart_document_week.json';

@Injectable()
export class ChartDocumentTestGuiStaticService {

    loadChartDocument(period: number) : ChartDocumentModel {
        if (period === 0) {
            return this.loadChartDocumentModelByYear();
        } else if (period === 1) {
            return this.loadChartDocumentModelByMonth();
        } else {
            return this.loadChartDocumentModelByWeek();
        }
    }

    private loadChartDocumentModelByMonth() : ChartDocumentModel {
        let chartDocumentModel: ChartDocumentModel = new ChartDocumentModel();
        let lineChart = Object.assign({}, data_month.chart_document);
        chartDocumentModel.lineChartData = lineChart.lineChartData;
        chartDocumentModel.lineChartLabels = lineChart.lineChartLabels;
        return chartDocumentModel;
    }

    private loadChartDocumentModelByYear() : ChartDocumentModel {
        let chartDocumentModel: ChartDocumentModel = new ChartDocumentModel();
        let lineChart = Object.assign({}, data_year.chart_document);
        chartDocumentModel.lineChartData = lineChart.lineChartData;
        chartDocumentModel.lineChartLabels = lineChart.lineChartLabels;
        return chartDocumentModel;
    }

    private loadChartDocumentModelByWeek() : ChartDocumentModel {
        let chartDocumentModel: ChartDocumentModel = new ChartDocumentModel();
        let lineChart = Object.assign({}, data_week.chart_document);
        chartDocumentModel.lineChartData = lineChart.lineChartData;
        chartDocumentModel.lineChartLabels = lineChart.lineChartLabels;
        return chartDocumentModel;
    }
}
