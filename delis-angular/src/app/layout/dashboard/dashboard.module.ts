import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from "@ngx-translate/core";
import { BsDatepickerModule } from "ngx-bootstrap";
import { ChartsModule } from "ng2-charts";

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard.component';
import { ChartDocumentComponent } from './components';
import { StatModule } from '../../shared';
import { ErrorsModule } from '../../shared';
import { DashboardService } from "./dashboard.service";
import { ChartDocumentService } from "./components/chart-document/services/chart.document.service";

@NgModule({
    imports: [
        CommonModule,
        ChartsModule,
        BsDatepickerModule,
        DashboardRoutingModule,
        TranslateModule,
        StatModule,
        ErrorsModule
    ],
    declarations: [
        DashboardComponent,
        ChartDocumentComponent
    ],
    providers: [ChartDocumentService, DashboardService]
})
export class DashboardModule {}
