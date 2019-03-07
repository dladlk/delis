import {Component, Input, OnInit} from "@angular/core";
import moment from 'moment';

import { routerTransition } from "../../../../router.animations";
import { DateRangeModel } from "../../../../models/date.range.model";
import { DATE_FORMAT } from "../../../../app.constants";
import { DaterangeService } from "./daterange.service";
import { DaterangeShowService } from "./daterange.show.service";
import { DateRangePicker } from "./date.range.picker";
import { PaginationService } from "../pagination/pagination.service";
import { PaginationModel } from "../pagination/pagination.model";

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
    dateRangeModel: DateRangeModel = new DateRangeModel();
    dateRange: DateRangePicker;
    alwaysShowCalendars: boolean;
    ranges: any = {
        'Today': [moment(), moment()],
        'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
        'Last 7 Days': [moment().subtract(6, 'days'), moment()],
        'Last 30 Days': [moment().subtract(29, 'days'), moment()],
        'This Month': [moment().startOf('month'), moment().endOf('month')],
        'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
    };

    constructor(private dtService: DaterangeService, private dtShowService: DaterangeShowService, private paginationService: PaginationService) {
        this.alwaysShowCalendars = true;
        this.paginationService.listen().subscribe((pag: PaginationModel) => {
            if (pag.collectionSize === 0) {
                this.dateRange = null;
            }
        });
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
