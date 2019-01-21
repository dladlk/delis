import { Component, OnInit } from '@angular/core';
import { routerTransition } from '../../router.animations';
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../service/locale.service";
import { ContentSelectInfoService } from "../../service/content.select.info.service";
import { RuntimeConfigService } from "../../service/runtime.config.service";
import { TokenService } from "../../service/token.service";

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss'],
    animations: [routerTransition()]
})
export class DashboardComponent implements OnInit {

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private contentSelectInfoService: ContentSelectInfoService,
        private configService: RuntimeConfigService,
        private tokenService: TokenService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }

    ngOnInit() {
        let url = this.configService.getConfigUrl();
        this.contentSelectInfoService.generateAllContentSelectInfo(url + '/table-info', this.tokenService.getToken());
    }
}
