import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { BsDatepickerModule } from "ngx-bootstrap";
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from "@ngx-translate/core";
import { NgSelectModule } from "@ng-select/ng-select";

import { BsComponentRoutingModule } from './bs-component-routing.module';
import { BsComponentComponent } from './bs-component.component';
import { PaginationComponent, TableHeaderSortComponent, ErrorComponent } from './components';
import { PageHeaderModule } from '../../shared';
import { PaginationService } from "./components/pagination/pagination.service";
import { DaterangeComponent } from "./components/daterange/daterange.component";
import { DaterangeService } from "./components/daterange/daterange.service";

@NgModule({
    imports: [
        CommonModule,
        BsComponentRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        NgbModule, NgSelectModule, BsDatepickerModule,
        PageHeaderModule, TranslateModule
    ],
    declarations: [
        BsComponentComponent,
        TableHeaderSortComponent,
        PaginationComponent,
        DaterangeComponent,
        ErrorComponent
    ],
    exports: [
        TableHeaderSortComponent,
        PaginationComponent,
        DaterangeComponent,
        ErrorComponent
    ],
    providers: [
        PaginationService, DaterangeService
    ]
})
export class BsComponentModule {}
