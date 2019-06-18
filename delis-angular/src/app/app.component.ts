import { Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from "ngx-spinner";
import { RuntimeConfigService } from "./service/runtime.config.service";
import { environment } from '../environments/environment';


@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

    version = environment.version;

    constructor(private configService: RuntimeConfigService, private spinner: NgxSpinnerService) {
    }

    ngOnInit() {
        let currentVersion = localStorage.getItem('appVersion');
        if (currentVersion === null || currentVersion !== this.version) {
            localStorage.clear();
            localStorage.setItem('appVersion', this.version);
        }
        this.configService.getUrl();
        this.spinner.show();
        setTimeout(() => {
            this.spinner.hide();
        }, 2000);
    }
}
