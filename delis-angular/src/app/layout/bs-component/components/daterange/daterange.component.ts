import { Component } from "@angular/core";
import { BsLocaleService, daLocale } from "ngx-bootstrap";
import { defineLocale } from 'ngx-bootstrap/chronos';
defineLocale('da', daLocale);

import { routerTransition } from "../../../../router.animations";
import { DateRangeModel } from "../../../../models/date.range.model";
import { DATE_FORMAT } from "../../../../app.constants";
import { DaterangeService } from "./daterange.service";

@Component({
    selector: 'app-daterange',
    templateUrl: './daterange.component.html',
    styleUrls: ['./daterange.component.scss'],
    animations: [routerTransition()]
})
export class DaterangeComponent {

    buttonName: any = 'Date period';
    show: boolean = false;
    clearableSelect = false;
    rangeDates: [];
    selectedDate: any;
    date: Date[];
    DATE_FORMAT = DATE_FORMAT;
    dateRangeModel: DateRangeModel = new DateRangeModel();

    constructor(private dtService: DaterangeService, private localeService: BsLocaleService,) {
        this.localeService.use('da');
        this.rangeDates = JSON.parse(localStorage.getItem("dateRanges"));
        this.selectedDate = "ALL";
    }

    toggle() {
        this.show = !this.show;
        if(this.show)
            this.buttonName = "Hide";
        else
            this.buttonName = "Date period";
    }

    loadRangeDate() {
        this.dateRangeModel = new DateRangeModel();
        if (this.selectedDate === 'Today') {
            this.dateRangeModel.dateStart = new Date();
            this.dateRangeModel.dateEnd = new Date();
            this.dateRangeModel.dateStart.setHours(0,0,0,0);
            this.dateRangeModel.dateEnd.setHours(23,59,59,999);
        }
        if (this.selectedDate === 'Yesterday') {
            this.dateRangeModel.dateStart = new Date();
            this.dateRangeModel.dateEnd = new Date();
            this.dateRangeModel.dateStart.setHours(0,0,0,0);
            this.dateRangeModel.dateEnd.setHours(23,59,59,999);
            this.dateRangeModel.dateStart.setDate(this.dateRangeModel.dateStart.getDate() - 1);
            this.dateRangeModel.dateEnd.setDate(this.dateRangeModel.dateEnd.getDate() - 1);
        }
        if (this.selectedDate === 'Last week') {
            this.dateRangeModel.dateStart = new Date();
            this.dateRangeModel.dateEnd = new Date();
            this.dateRangeModel.dateEnd.setHours(23,59,59,999);
            this.dateRangeModel.dateStart.setDate(this.getMonday(this.dateRangeModel.dateStart).getDate());
            this.dateRangeModel.dateStart.setHours(0,0,0,0);
        }
        if (this.selectedDate === 'Last month') {
            this.dateRangeModel.dateStart = new Date();
            this.dateRangeModel.dateEnd = new Date();
            this.dateRangeModel.dateEnd.setHours(23,59,59,999);
            this.dateRangeModel.dateStart.setDate(this.getFirstDayOfMonth(this.dateRangeModel.dateStart).getDate());
            this.dateRangeModel.dateStart.setHours(0,0,0,0);
        }
        this.dtService.loadDate(this.dateRangeModel);
    }

    loadReceivedDate(date: Date[]) {
        if (date !== null) {
            date[0].setHours(0,0,0,0);
            date[1].setHours(23,59,59,999);
            this.dateRangeModel.dateStart = date[0];
            this.dateRangeModel.dateEnd = date[1];
        } else {
            this.dateRangeModel = new DateRangeModel();
        }
        this.dtService.loadDate(this.dateRangeModel);
    }

    private getMonday(date: Date) : Date {
        let day = date.getDay() || 7;
        if( day !== 1 )
            date.setHours(-24 * (day - 1));
        return date;
    }

    private getFirstDayOfMonth(date: Date) : Date {
        return new Date(date.getFullYear(), date.getMonth(), 1);
    }
}
