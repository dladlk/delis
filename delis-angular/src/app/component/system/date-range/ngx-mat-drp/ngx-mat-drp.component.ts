import {
  Component,
  OnInit,
  ViewChild,
  Output,
  EventEmitter,
  Input,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { OverlayRef } from '@angular/cdk/overlay';
import { Subscription } from 'rxjs';

import { RangeStoreService } from '../service/range-store.service';
import { ConfigStoreService } from '../service/config-store.service';
import { CalendarOverlayService } from '../service/calendar-overlay.service';
import { Range, NgxDrpOptions, RangeUpdate } from '../model/model';
import { DaterangeObservable } from '../../../../observable/daterange.observable';
import { ResetDaterangeObservable } from "../../../../observable/reset-daterange.observable";
import { ResetDaterangeForTodayObservable } from "../../../../observable/reset-daterange-for-today.observable";

@Component({
  selector: 'app-ngx-mat-drp',
  templateUrl: './ngx-mat-drp.component.html',
  styleUrls: ['./ngx-mat-drp.component.scss'],
  providers: [
    CalendarOverlayService,
    RangeStoreService,
    ConfigStoreService,
    DatePipe
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NgxMatDrpComponent implements OnInit, OnDestroy {

  @ViewChild('calendarInput', {static: true}) calendarInput;
  @Output() readonly selectedDateRangeChanged: EventEmitter<Range> = new EventEmitter<Range>();
  @Input() options: NgxDrpOptions;
  @Input() appearance: string;
  private rangeUpdate$: Subscription;
  private rangeReset$: Subscription;
  private rangeResetForToday$: Subscription;
  selectedDateRange = null;

  constructor(
    private changeDetectionRef: ChangeDetectorRef,
    private calendarOverlayService: CalendarOverlayService,
    public rangeStoreService: RangeStoreService,
    public configStoreService: ConfigStoreService,
    private datePipe: DatePipe,
    private daterangeObservable: DaterangeObservable,
    private resetDaterangeObservable: ResetDaterangeObservable,
    private resetDaterangeForTodayObservable: ResetDaterangeForTodayObservable) {}

  ngOnInit() {
    this.configStoreService.ngxDrpOptions = this.options;
    this.rangeReset$ = this.resetDaterangeObservable.listen().subscribe(() =>  this.resetDates());
    this.rangeResetForToday$ = this.resetDaterangeForTodayObservable.listen().subscribe(() =>  this.resetDatesForToday());
    this.rangeUpdate$ = this.rangeStoreService.rangeUpdate$.subscribe(range => {
      this.formatSelectedDateRange(range.range);
      this.selectedDateRangeChanged.emit(range.range);
      if (range.update) {
        this.daterangeObservable.loadDate(range.range);
      }
    });
    if (this.options.range === null) {
      this.selectedDateRange = 'td.picker.all';
    } else {
      this.formatSelectedDateRange(this.options.range);
    }
    this.changeDetectionRef.detectChanges();
  }

  formatSelectedDateRange(range: Range) {
    if (range.fromDate !== null && range.toDate !== null) {
      const from: string = this.formatToDateString(range.fromDate,this.options.format);
      const to: string = this.formatToDateString(range.toDate,this.options.format);
      let rangeTitle = `${from} - ${to}`;
      if (from === to) {
        rangeTitle = from;

        const todayFormat = this.formatToDateString(new Date(),this.options.format);
        if (from === todayFormat) {
          rangeTitle = 'td.picker.today';
        }
      }
      this.selectedDateRange = rangeTitle;
    } else {
      this.selectedDateRange = 'td.picker.all';
    }
  }

  ngOnDestroy() {
    if (this.rangeUpdate$) {
      this.rangeUpdate$.unsubscribe();
    }
    if (this.rangeReset$) {
      this.rangeReset$.unsubscribe();
    }
    if (this.rangeResetForToday$) {
      this.rangeResetForToday$.unsubscribe();
    }
  }

  private formatToDateString(date: Date, format: string): string {
    return this.datePipe.transform(date, format);
  }

  openCalendar(event) {
    const overlayRef: OverlayRef = this.calendarOverlayService.open(
      this.options.calendarOverlayConfig,
      this.calendarInput
    );
  }

  public resetDates() {
    const rangeUpdate = new RangeUpdate();
    rangeUpdate.range = {fromDate: null, toDate: null};
    rangeUpdate.update = false;
    this.rangeStoreService.updateRange(rangeUpdate);
  }

  public resetDatesForToday() {
    const rangeUpdate = new RangeUpdate();
    rangeUpdate.range = {fromDate: new Date(), toDate: new Date()};
    rangeUpdate.update = false;
    this.rangeStoreService.updateRange(rangeUpdate);
  }
}
