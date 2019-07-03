import { browser, by, element } from 'protractor/built';

export class AppPage {
  navigateTo() {
    // @ts-ignore
    return browser.get(browser.baseUrl) as Promise<any>;
  }

  getTitleText() {
    // @ts-ignore
    return element(by.css('app-root h1')).getText() as Promise<string>;
  }
}
