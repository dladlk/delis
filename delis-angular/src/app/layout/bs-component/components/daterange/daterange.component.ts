import { Component, Input, OnInit } from "@angular/core";
import { LocaleConfig } from "ngx-daterangepicker-material";
import moment from 'moment';

moment.locale('da');

import { routerTransition } from "../../../../router.animations";
import { DATE_FORMAT } from "../../../../app.constants";
import { FIRST_DAY } from "../../../../app.constants";
import { DaterangeService} from "./daterange.service";
import { DateRangePicker } from "./date.range.picker";
import { ForwardingLanguageService } from "../../../../service/forwarding.language.service";
import { LocaleService } from "../../../../service/locale.service";

@Component({
    selector: 'app-daterange',
    templateUrl: './daterange.component.html',
    styleUrls: ['./daterange.component.scss'],
    animations: [routerTransition()]
})
export class DaterangeComponent implements OnInit {

    @Input() drops: string;
    @Input() opens: string;
    @Input() dateRangeModel: DateRangePicker;
    @Input() isCanInit = false;

    localeConfig: LocaleConfig;
    dateRange: any;
    alwaysShowCalendars: boolean;
    lang: string;

    ranges: any = {};

    applyButtonEN = "APPLAY";
    applyButtonDA = "ANSØGE";
    applyButton: string;

    customRange: string;
    customRangeDA = "Defineret";
    customRangeEN = "Custom Range";

    rangesEN: any = {
        'Today': [moment(), moment()],
        'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
        'Last 7 Days': [moment().subtract(6, 'days'), moment()],
        'Last 30 Days': [moment().subtract(29, 'days'), moment()],
        'This Month': [moment().startOf('month'), moment().endOf('month')],
        'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
    };

    rangesDA: any = {
        'I dag': [moment(), moment()],
        'I går': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
        'Sidste 7 dage': [moment().subtract(6, 'days'), moment()],
        'Sidste 30 dage': [moment().subtract(29, 'days'), moment()],
        'Denne måned': [moment().startOf('month'), moment().endOf('month')],
        'Sidste måned': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
    };

    constructor(
        private localeService: LocaleService,
        private forwardingLanguageService: ForwardingLanguageService,
        private dtService: DaterangeService) {
        this.lang = localeService.getlocale().match(/en|da/) ? localeService.getlocale() : 'en';
        this.forwardingLanguageService.listen().subscribe((lang: string) => {
            this.lang = lang;
            this.initLocale(this.lang);
            this.initLocaleConfig();
        });
        this.alwaysShowCalendars = true;
        this.initLocale(this.lang);
        this.initLocaleConfig();
    }

    initLocale(lang: string) {
        if ('da' === lang) {
            this.ranges = this.rangesDA;
            this.applyButton = this.applyButtonDA;
            this.customRange = this.customRangeDA;
        } else {
            this.ranges = this.rangesEN;
            this.applyButton = this.applyButtonEN;
            this.customRange = this.customRangeEN;
        }
    }

    initLocaleConfig() {
        this.localeConfig = {
            format: DATE_FORMAT,
            daysOfWeek: moment.weekdaysMin(),
            monthNames: moment.monthsShort(),
            firstDay: FIRST_DAY,
            clearLabel: 'Klar',
            applyLabel: this.applyButton,
            customRangeLabel: "Defineret"
        };
    }

    change() {
        if (this.dateRange !== undefined && this.isCanInit) {
            this.dateRangeModel = this.dateRange;
            this.dtService.loadDate(this.dateRangeModel);
        }
    }

    ngOnInit(): void {
        if (this.dateRangeModel !== null) {
            var start = new Date(this.dateRangeModel.startDate);
            var end = new Date(this.dateRangeModel.endDate);
            this.dateRange = {
                startDate: moment(start),
                endDate: moment(end),
            }
        }
    }

    canInit() {
        this.isCanInit = true;
    }
}
