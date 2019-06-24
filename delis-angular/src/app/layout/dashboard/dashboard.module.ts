import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from "@ngx-translate/core";
import { ChartsModule } from "ng2-charts";

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard.component';
import { ChartDocumentComponent } from './components';
import { StatModule } from '../../shared';
import { DashboardService } from "./dashboard.service";
import { ChartDocumentService } from "./components/chart-document/services/chart.document.service";
import { BsComponentModule } from "../bs-component/bs-component.module";
import { DaterangeService } from "../bs-component/components/daterange/daterange.service";

@NgModule({
    imports: [
        CommonModule,
        ChartsModule,
        DashboardRoutingModule,
        TranslateModule,
        StatModule,
        BsComponentModule
    ],
    declarations: [
        DashboardComponent,
        ChartDocumentComponent
    ],
    providers: [ChartDocumentService, DashboardService, DaterangeService]
})
export class DashboardModule {}
