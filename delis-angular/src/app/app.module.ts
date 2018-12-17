import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthGuard } from './shared';
import { TokenService } from './service/token.service';
import { AuthorizationService } from './login/authorization.service';
import { HttpService } from './service/http.service';
import { HttpErrorService } from './service/http.error.service';
import { BsDatepickerModule } from 'ngx-bootstrap';

export const createTranslateLoader = (http: HttpClient) => {

    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
};

@NgModule({
    imports: [
        CommonModule,
        BrowserModule,
        BrowserAnimationsModule,
        ReactiveFormsModule,
        HttpClientModule,
        BsDatepickerModule.forRoot(),
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: createTranslateLoader,
                deps: [HttpClient]
            }
        }),
        AppRoutingModule
    ],
    declarations: [AppComponent],
    providers: [
      AuthGuard,
      TokenService,
      AuthorizationService,
      HttpService,
      HttpErrorService],
    bootstrap: [AppComponent],
    exports: [BsDatepickerModule]
})
export class AppModule {}
