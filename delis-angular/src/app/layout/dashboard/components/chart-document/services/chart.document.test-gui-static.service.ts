import { Injectable } from "@angular/core";
import { ChartDocumentModel } from "../model/chart.document.model";
import * as data from '../chart_document.json';

@Injectable()
export class ChartDocumentTestGuiStaticService {

    constructor() {}

    loadChartDocumentModel() : ChartDocumentModel {
        let chartDocumentModel: ChartDocumentModel = new ChartDocumentModel();
        let lineChartData = data.chart_document.lineChartData;
        let lineChartLabels = data.chart_document.lineChartLabels;
        chartDocumentModel.lineChartData = lineChartData;
        chartDocumentModel.lineChartLabels = lineChartLabels;
        return chartDocumentModel;
    }
}
