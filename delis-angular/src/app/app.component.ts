import { Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from "ngx-spinner";
import { RuntimeConfigService } from "./service/runtime.config.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

    constructor(private configService: RuntimeConfigService, private spinner: NgxSpinnerService) {
    }

    ngOnInit() {
        this.configService.getUrl();
        this.spinner.show();
        setTimeout(() => {
            this.spinner.hide();
        }, 2000);
    }
}
