import { Component, OnInit } from '@angular/core';
import { RuntimeConfigService } from "./service/runtime.config.service";
import { NgxSpinnerService } from "ngx-spinner";

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
            /** spinner ends after 5 seconds */
            this.spinner.hide();
        }, 2000);
    }
}
