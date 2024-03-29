import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LayoutModule } from '@angular/cdk/layout';
import { OverlayModule } from '@angular/cdk/overlay';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DateAdapter } from '@angular/material/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import {ChartsModule, ThemeService} from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { DelisMaterialModule } from './core/delis-material.module';

import { AppComponent } from './app.component';

import { AutofocusDirective } from './directive/autofocus.directive';
import { MaterialElevationDirective } from './directive/material-elevation.directive';
import { CustomDateAdapter} from './component/system/date-range/calendar-wrapper/custom-date-adapter';
import { HttpEventInterceptor} from './component/interceptor/HttpEventInterceptor';
import { PaginatorI18n} from './i18n/paginator-I18n';

import { NotFoundComponent } from './component/not-found/not-found.component';
import { LayoutComponent } from './component/layout/layout.component';
import { TopnavComponent } from './component/layout/topnav/topnav.component';
import { SidebarComponent } from './component/layout/sidebar/sidebar.component';
import { DashboardComponent } from './component/content/dashboard/dashboard.component';
import { LoginComponent } from './component/login/login.component';
import { LogoutComponent } from './component/logout/logout.component';
import { StatComponent } from './component/system/stat/stat.component';
import { ErrorComponent } from './component/system/error/error.component';
import { ChartDocumentComponent } from './component/system/chart-document/chart-document.component';

import { DateRangeComponent } from './component/system/date-range/date-range.component';
import { PickerOverlayComponent } from './component/system/date-range/picker-overlay/picker-overlay.component';
import { NgxMatDrpComponent } from './component/system/date-range/ngx-mat-drp/ngx-mat-drp.component';
import { MatDrpPresetsComponent } from './component/system/date-range/mat-drp-presets/mat-drp-presets.component';
import { CalendarWrapperComponent } from './component/system/date-range/calendar-wrapper/calendar-wrapper.component';

import { DelisTableDetailsHeaderComponent } from './component/system/delis-table-details-header/delis-table-details-header.component';
import { DelisTableDetailsFooterComponent } from './component/system/delis-table-details-footer/delis-table-details-footer.component';
import { DelisDataTableComponent } from './component/system/delis-data-table/delis-data-table.component';
import { DocumentComponent } from './component/content/document/document.component';
import { IdentifierComponent } from './component/content/identifier/identifier.component';
import { SendDocumentComponent } from './component/content/send-document/send-document.component';
import { DocumentDetailsComponent } from './component/content/document/document-details/document-details.component';
import { IrFormComponent } from './component/content/document/ir-form/ir-form.component';
import { IdentifierDetailsComponent } from './component/content/identifier/identifier-details/identifier-details.component';
import { SendDocumentDetailsComponent } from './component/content/send-document/send-document-details/send-document-details.component';

import { DATE } from './component/system/date-range/service/range-store.service';

export const createTranslateLoader = (http: HttpClient) => {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
};

@NgModule({
  declarations: [
    AutofocusDirective,
    MaterialElevationDirective,
    AppComponent,
    NotFoundComponent,
    LayoutComponent,
    TopnavComponent,
    SidebarComponent,
    DashboardComponent,
    LoginComponent,
    LogoutComponent,
    StatComponent,
    ErrorComponent,
    ChartDocumentComponent,
    DateRangeComponent,
    PickerOverlayComponent,
    NgxMatDrpComponent,
    MatDrpPresetsComponent,
    CalendarWrapperComponent,
    DelisDataTableComponent,
    DelisTableDetailsHeaderComponent,
    DelisTableDetailsFooterComponent,
    DocumentComponent,
    IdentifierComponent,
    SendDocumentComponent,
    DocumentDetailsComponent,
    IrFormComponent,
    IdentifierDetailsComponent,
    SendDocumentDetailsComponent
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    DelisMaterialModule,
    AppRoutingModule,
    LayoutModule,
    OverlayModule,
    HttpClientModule,
    FlexLayoutModule,
    ChartsModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient]
      }
    })
  ],
  // entryComponents: [PickerOverlayComponent],
  providers: [
    ThemeService,
    {
      provide: DATE, useValue: new Date()
    },
    {
      provide: DateAdapter, useClass: CustomDateAdapter
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
