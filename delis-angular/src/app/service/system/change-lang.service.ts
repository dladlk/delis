import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { LocaleService } from './locale.service';
import { ForwardingLanguageObservable } from '../../observable/forwarding-language.observable';
import { ContentSelectInfoService } from './content-select-info.service';
import { TokenService } from './token.service';
import { RefreshObservable } from '../../observable/refresh.observable';

@Injectable({
  providedIn: 'root'
})
export class ChangeLangService {

  public lang: string;

  constructor(
    private translate: TranslateService,
    private locale: LocaleService,
    private tokenService: TokenService,
    private contentSelectInfoService: ContentSelectInfoService,
    private forwardingLanguageObservable: ForwardingLanguageObservable,
    private refreshObservable: RefreshObservable) { }

  changeLang(language: string): string {
    this.translate.use(language);
    this.translate.setDefaultLang(language);
    this.locale.setLocale(language);
    this.lang = language;
    this.forwardingLanguageObservable.forwardLanguage(this.lang);
    this.contentSelectInfoService.generateAllContentSelectInfo(this.tokenService.getToken());
    this.contentSelectInfoService.generateUniqueOrganizationNameInfo(this.tokenService.getToken());
    this.refreshObservable.refreshPage();
    return this.lang;
  }
}
