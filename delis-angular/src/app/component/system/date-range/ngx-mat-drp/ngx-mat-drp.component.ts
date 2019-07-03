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
import { Range, NgxDrpOptions } from '../model/model';
import {DaterangeObservable} from '../../../../observable/daterange.observable';

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
  private rangeUpdate$: Subscription;
  selectedDateRange = null;

  constructor(
    private changeDetectionRef: ChangeDetectorRef,
    private calendarOverlayService: CalendarOverlayService,
    public rangeStoreService: RangeStoreService,
    public configStoreService: ConfigStoreService,
    private datePipe: DatePipe,
    private daterangeObservable: DaterangeObservable) {}

  ngOnInit() {
    this.configStoreService.ngxDrpOptions = this.options;
    this.rangeUpdate$ = this.rangeStoreService.rangeUpdate$.subscribe(range => {
      if (range.fromDate !== null && range.toDate !== null) {
        const from: string = this.formatToDateString(
          range.fromDate,
          this.options.format
        );
        const to: string = this.formatToDateString(
          range.toDate,
          this.options.format
        );
        this.selectedDateRange = `${from} - ${to}`;
      } else {
        this.selectedDateRange = '';
      }
      this.selectedDateRangeChanged.emit(range);
      this.daterangeObservable.loadDate(range);
    });
    if (this.options.range !== null) {
      this.rangeStoreService.updateRange(
        this.options.range.fromDate,
        this.options.range.toDate
      );
    }
    this.changeDetectionRef.detectChanges();
  }

  ngOnDestroy() {
    if (this.rangeUpdate$) {
      this.rangeUpdate$.unsubscribe();
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

  public resetDates(range: Range) {
    this.rangeStoreService.updateRange(
      range.fromDate,
      range.toDate
    );
  }
}
