import { Component, Input, OnInit } from "@angular/core";
import moment from 'moment';

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

@Component({
    selector: 'app-daterange',
    templateUrl: './daterange.component.html',
    styleUrls: ['./daterange.component.scss'],
    animations: [routerTransition()]
})
export class DaterangeComponent implements OnInit {

    @Input() drops: string;
    @Input() opens: string;

    DATE_FORMAT = DATE_FORMAT;
    FIRST_DAY = FIRST_DAY;

    dateRangeModel: DateRangeModel = new DateRangeModel();
    dateRange: DateRangePicker;
    alwaysShowCalendars: boolean;

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
        private localeService: LocaleService,
        private forwardingLanguageService: ForwardingLanguageService,
        private dtService: DaterangeService,
        private dtShowService: DaterangeShowService,
        private paginationService: PaginationService) {
        let lang = localeService.getlocale().match(/en|da/) ? localeService.getlocale() : 'en';
        this.getRange(lang);
        this.forwardingLanguageService.listen().subscribe((lang: string) => {
           this.getRange(lang);
        });
        this.alwaysShowCalendars = true;
        this.paginationService.listen().subscribe((pag: PaginationModel) => {
            if (pag.collectionSize === 0) {
                this.dateRange = null;
            }
        });
    }

    getRange(lang: string) {
        if ('da' === lang) {
            this.ranges = this.rangesDA;
        } else {
            this.ranges = this.rangesEN;
        }
    }

    change(dateRange: DateRangePicker) {
        if (dateRange !== null) {
            if ((dateRange.startDate !== null && dateRange.endDate !== null)) {
                this.dateRangeModel.dateStart = new Date(dateRange.startDate);
                this.dateRangeModel.dateEnd = new Date(dateRange.endDate);
                this.dtService.loadDate(this.dateRangeModel);
            } else {
                this.dtShowService.hide(true);
            }
        }
    }

    ngOnInit(): void {
    }
}
