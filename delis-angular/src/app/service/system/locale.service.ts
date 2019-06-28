import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LocaleService {

  private LOCALE_LANG = 'locale_lang';

  constructor() { }

  setLocale(locale: string) {
    localStorage.setItem(this.LOCALE_LANG, locale);
  }

  getLocale(): string {
    const locale = localStorage.getItem(this.LOCALE_LANG);
    if (locale === null) {
      this.setLocale('da');
      return this.getLocale();
    }
    return locale;
  }
}
