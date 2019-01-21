import { Component, OnInit } from '@angular/core';
import { RuntimeConfigService } from "./service/runtime.config.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

    constructor(private configService: RuntimeConfigService) {
    }

    ngOnInit() {
        this.configService.getUrl();
    }
}
