import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { FormsModule } from '@angular/forms';
import { NgxDaterangepickerMd } from 'ngx-daterangepicker-material';
import { NgSelectModule } from '@ng-select/ng-select';
import { NgbDropdownModule, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ChartsModule } from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './component/header/header.component';
import { LoginComponent } from './component/login/login.component';
import { LayoutComponent } from './component/layout/layout.component';
import { HttpEventInterceptor } from './interceptor/http-event.interceptor';
import { StatComponent } from './component/stat/stat.component';
import { DashboardComponent } from './component/dashboard/dashboard.component';
import { DocumentComponent } from './component/document/document.component';
import { SendDocumentComponent } from './component/send-document/send-document.component';
import { SendDocumentDetailsComponent } from './component/send-document/send-document-details/send-document-details.component';
import { DocumentDetailsComponent } from './component/document/document-details/document-details.component';
import { IdentifierComponent } from './component/identifier/identifier.component';
import { IdentifierDetailsComponent } from './component/identifier/identifier-details/identifier-details.component';
import { NotFoundComponent } from './component/not-found/not-found.component';
import { ErrorComponent } from './component/system/error/error.component';
import { DaterangeComponent } from './component/system/daterange/daterange.component';
import { PaginationComponent } from './component/system/pagination/pagination.component';
import { TableHeaderSortComponent } from './component/system/table-header-sort/table-header-sort.component';
import { InvoiceComponent } from './component/document/invoice/invoice.component';
import { ChartDocumentComponent } from './component/chart-document/chart-document.component';

export const createTranslateLoader = (http: HttpClient) => {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
};

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    LoginComponent,
    LayoutComponent,
    StatComponent,
    DashboardComponent,
    DocumentComponent,
    SendDocumentComponent,
    SendDocumentDetailsComponent,
    DocumentDetailsComponent,
    IdentifierComponent,
    IdentifierDetailsComponent,
    NotFoundComponent,
    ErrorComponent,
    DaterangeComponent,
    PaginationComponent,
    TableHeaderSortComponent,
    InvoiceComponent,
    ChartDocumentComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient]
      }
    }),
    NgxDaterangepickerMd.forRoot(),
    FormsModule,
    NgbDropdownModule,
    NgbModule,
    NgSelectModule,
    ChartsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpEventInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
