import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { OverlayModule } from '@angular/cdk/overlay';
import { MatPaginatorIntl } from "@angular/material";
import { ChartsModule } from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { DelisMaterialModule } from './core/delis-material.module';
import { AppComponent } from './app.component';
import { LayoutComponent } from './component/layout/layout.component';
import { HeaderComponent } from './component/navigation/header/header.component';
import { DashboardComponent } from './component/content/dashboard/dashboard.component';
import { LoginComponent } from './component/login/login.component';
import { NotFoundComponent } from './component/not-found/not-found.component';
import { IdentifierComponent } from './component/content/identifier/identifier.component';
import { DocumentComponent } from './component/content/document/document.component';
import { SendDocumentComponent } from './component/content/send-document/send-document.component';
import { IdentifierDetailsComponent } from './component/content/identifier/identifier-details/identifier-details.component';
import { DocumentDetailsComponent } from './component/content/document/document-details/document-details.component';
import { SendDocumentDetailsComponent } from './component/content/send-document/send-document-details/send-document-details.component';
import { SidenavListComponent } from './component/navigation/sidenav-list/sidenav-list.component';
import { DateRangeComponent } from './component/system/date-range/date-range.component';
import { CalendarWrapperComponent } from './component/system/date-range/calendar-wrapper/calendar-wrapper.component';
import { NgxMatDrpComponent } from './component/system/date-range/ngx-mat-drp/ngx-mat-drp.component';
import { PickerOverlayComponent } from './component/system/date-range/picker-overlay/picker-overlay.component';
import { MatDrpPresetsComponent } from './component/system/date-range/mat-drp-presets/mat-drp-presets.component';
import { HttpEventInterceptor } from './component/interceptor/HttpEventInterceptor';
import { DATE } from './component/system/date-range/service/range-store.service';
import { ErrorComponent } from './component/system/error/error.component';
import { InvoiceComponent } from './component/content/document/invoice/invoice.component';
import { AutofocusDirective } from './directive/autofocus.directive';
import { LogoutComponent } from './component/logout/logout.component';
import { StatComponent } from './component/system/stat/stat.component';
import { ChartDocumentComponent } from './component/system/chart-document/chart-document.component';
import { PaginatorI18n } from "./i18n/paginator-I18n";
import { DelisDataTableComponent } from './component/system/delis-data-table/delis-data-table.component';
import { DelisTableDetailsHeaderComponent } from './component/system/delis-table-details-header/delis-table-details-header.component';

export const createTranslateLoader = (http: HttpClient) => {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
};

@NgModule({
  declarations: [
    AppComponent,
    LayoutComponent,
    HeaderComponent,
    DashboardComponent,
    LoginComponent,
    NotFoundComponent,
    IdentifierComponent,
    DocumentComponent,
    SendDocumentComponent,
    IdentifierDetailsComponent,
    DocumentDetailsComponent,
    SendDocumentDetailsComponent,
    SidenavListComponent,
    DateRangeComponent,
    CalendarWrapperComponent,
    NgxMatDrpComponent,
    PickerOverlayComponent,
    MatDrpPresetsComponent,
    ErrorComponent,
    InvoiceComponent,
    AutofocusDirective,
    LogoutComponent,
    StatComponent,
    ChartDocumentComponent,
    DelisDataTableComponent,
    DelisTableDetailsHeaderComponent
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    HttpClientModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FlexLayoutModule,
    DelisMaterialModule,
    OverlayModule,
    ChartsModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient]
      }
    }),
  ],
  entryComponents: [PickerOverlayComponent],
  providers: [
    {
      provide: DATE, useValue: new Date()
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpEventInterceptor,
      multi: true
    },
    {
      provide: MatPaginatorIntl,
      useFactory: (translate) => {
        const service = new PaginatorI18n();
        service.injectTranslateService(translate);
        return service;
      },
      deps: [TranslateService]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
