import { Injectable } from "@angular/core";

@Injectable()
export class LocaleService {

    private LOCALE_LANG = "locale_lang";

    setLocale(locale: string) {
        localStorage.setItem(this.LOCALE_LANG, locale);
    }

    getlocale(): string {
        let locale = localStorage.getItem(this.LOCALE_LANG);
        if (locale === null) {
            this.setLocale('en');
            return this.getlocale();
        }
        return locale;
    }
}
