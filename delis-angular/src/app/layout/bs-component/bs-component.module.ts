import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { BsComponentRoutingModule } from './bs-component-routing.module';
import { BsComponentComponent } from './bs-component.component';
import {
    AlertComponent,
    ButtonsComponent,
    ModalComponent,
    CollapseComponent,
    DatePickerComponent,
    DropdownComponent,
    PopOverComponent,
    ProgressbarComponent,
    TabsComponent,
    RatingComponent,
    TooltipComponent,
    TimepickerComponent
} from './components';
import { PageHeaderModule } from '../../shared';
import { TableHeaderSortComponent } from "./components/table-header-sort/table.header.sort.component";
import { TranslateModule } from "@ngx-translate/core";
import { PaginationComponent } from "./components/pagination/pagination.component";
import {NgSelectModule} from "@ng-select/ng-select";
import {PaginationService} from "./components/pagination/pagination.service";

@NgModule({
    imports: [
        CommonModule,
        BsComponentRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        NgbModule, NgSelectModule,
        PageHeaderModule, TranslateModule
    ],
    declarations: [
        BsComponentComponent,
        ButtonsComponent,
        AlertComponent,
        ModalComponent,
        CollapseComponent,
        DatePickerComponent,
        DropdownComponent,
        PopOverComponent,
        ProgressbarComponent,
        TabsComponent,
        RatingComponent,
        TooltipComponent,
        TimepickerComponent,
        TableHeaderSortComponent, PaginationComponent
    ], exports: [
        TableHeaderSortComponent, PaginationComponent
    ], providers: [PaginationService]
})
export class BsComponentModule {}
