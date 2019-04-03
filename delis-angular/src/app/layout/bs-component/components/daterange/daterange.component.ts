import { Component, Input, OnInit } from "@angular/core";
import { LocaleConfig} from "ngx-daterangepicker-material";
import moment from 'moment';
moment.locale('da');

import { routerTransition } from "../../../../router.animations";
import { DateRangeModel } from "../../../../models/date.range.model";
import { DATE_FORMAT } from "../../../../app.constants";
import { FIRST_DAY } from "../../../../app.constants";
import { DaterangeService } from "./daterange.service";
import { DaterangeShowService } from "./daterange.show.service";
import { DateRangePicker } from "./date.range.picker";
import { PaginationService } from "../pagination/pagination.service";
import { PaginationModel } from "../pagination/pagination.model";
import { ForwardingLanguageService } from "../../../../service/forwarding.language.service";
import { LocaleService } from "../../../../service/locale.service";
import {TranslateService} from "@ngx-translate/core";

@Component({
    selector: 'app-daterange',
    templateUrl: './daterange.component.html',
    styleUrls: ['./daterange.component.scss'],
    animations: [routerTransition()]
})
export class DaterangeComponent implements OnInit {

    @Input() drops: string;
    @Input() opens: string;

    localeConfig: LocaleConfig;
    dateRangeModel: DateRangeModel = new DateRangeModel();
    dateRange: DateRangePicker;
    alwaysShowCalendars: boolean;
    lang: string;

    ranges: any = {};

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
        private translate: TranslateService,
        private localeService: LocaleService,
        private forwardingLanguageService: ForwardingLanguageService,
        private dtService: DaterangeService,
        private dtShowService: DaterangeShowService,
        private paginationService: PaginationService) {

        this.lang = localeService.getlocale().match(/en|da/) ? localeService.getlocale() : 'en';
        this.translate.use(this.lang);

        this.forwardingLanguageService.listen().subscribe((lang: string) => {
            this.lang = lang;
           this.initLocale(this.lang);
        });
        this.alwaysShowCalendars = true;
        this.paginationService.listen().subscribe((pag: PaginationModel) => {
            if (pag.collectionSize === 0) {
                this.dateRange = null;
            }
        });


        this.initLocaleConfig();
        this.initLocale(this.lang);
    }

    initLocale(lang: string) {
        if ('da' === lang) {
            this.ranges = this.rangesDA;
        } else {
            this.ranges = this.rangesEN;
        }
    }

    initLocaleConfig() {
        this.localeConfig = {
            format: DATE_FORMAT,
            daysOfWeek: moment.weekdaysMin(),
            monthNames: moment.monthsShort(),
            firstDay: FIRST_DAY
        };
    }

    change(dateRange: DateRangePicker) {
        if (dateRange.startDate !== null && dateRange.endDate !== null) {
            this.dateRangeModel.dateStart = new Date(dateRange.startDate);
            this.dateRangeModel.dateEnd = new Date(dateRange.endDate);
            this.dtService.loadDate(this.dateRangeModel);
        } else {
            if (this.dateRange !== null && this.dateRangeModel.dateStart !== undefined && this.dateRangeModel.dateEnd !== undefined) {
                this.dtShowService.hide(true);
            }
        }
    }

    ngOnInit(): void {
    }
}
