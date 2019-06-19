import {CommonModule} from '@angular/common';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {NgxSpinnerModule} from 'ngx-spinner';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {AuthGuard} from './shared';
import {TokenService} from './service/token.service';
import {AuthorizationService} from './login/authorization.service';
import {LocaleService} from './service/locale.service';
import {RuntimeConfigService} from './service/runtime.config.service';
import {HttpRestService} from './service/http.rest.service';
import {ContentSelectInfoService} from './service/content.select.info.service';
import {ErrorService} from './service/error.service';
import {LogoutService} from './logout/logout.service';
import {ForwardingLanguageService} from './service/forwarding.language.service';
import {HttpEventInterceptor} from './service/http.event.interceptor';
import {RefreshTokenService} from './service/refresh.token.service';
import {FileSaverService} from './service/file.saver.service';
import {LocalStorageService} from './service/local.storage.service';

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
        NgxSpinnerModule,
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
        {
            provide: HTTP_INTERCEPTORS,
            useClass: HttpEventInterceptor,
            multi: true
        },
        AuthGuard,
        TokenService,
        RuntimeConfigService,
        AuthorizationService,
        ContentSelectInfoService,
        HttpRestService,
        LocaleService,
        ErrorService,
        LogoutService,
        ForwardingLanguageService,
        RefreshTokenService,
        FileSaverService,
        LocalStorageService],
    bootstrap: [AppComponent]
})
export class AppModule {
}
