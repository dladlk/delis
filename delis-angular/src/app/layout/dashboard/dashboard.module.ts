import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbCarouselModule, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from "@ngx-translate/core";
import { ChartsModule as Ng2Charts } from "ng2-charts";

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard.component';
import {
    TimelineComponent,
    NotificationComponent,
    ChatComponent,
    ChartDocumentComponent
} from './components';
import { StatModule } from '../../shared';
import { ErrorsModule } from '../../shared';
import { ChartDocumentTestGuiStaticService } from "./components/chart-document/services/chart.document.test-gui-static.service";

@NgModule({
    imports: [
        CommonModule,
        NgbCarouselModule,
        NgbAlertModule,
        Ng2Charts,
        DashboardRoutingModule,
        TranslateModule,
        StatModule,
        ErrorsModule
    ],
    declarations: [
        DashboardComponent,
        TimelineComponent,
        NotificationComponent,
        ChatComponent,
        ChartDocumentComponent
    ],
    providers: [ChartDocumentTestGuiStaticService]
})
export class DashboardModule {}
