import {
  Component,
  ViewChild,
  Output,
  Input,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  ChangeDetectionStrategy
} from '@angular/core';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatCalendar } from '@angular/material';
import { MAT_MOMENT_DATE_FORMATS, MomentDateAdapter } from '@angular/material-moment-adapter';
import { ConfigStoreService } from '../service/config-store.service';
import { LocaleService} from '../../../../service/system/locale.service';

@Component({
  selector: 'app-calendar-wrapper',
  templateUrl: './calendar-wrapper.component.html',
  styleUrls: ['./calendar-wrapper.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // providers: [
  //   {
  //     provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]
  //   },
  //   {
  //     provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS
  //   },
  // ]
})
export class CalendarWrapperComponent implements OnChanges {

  @ViewChild(MatCalendar, {static: true}) matCalendar: MatCalendar<Date>;

  @Output()
  readonly selectedDateChange: EventEmitter<Date> = new EventEmitter<Date>();

  dateFormat: string;
  @Input() selectedDate: Date;
  @Input() prefixLabel: string;
  @Input() minDate: Date;
  @Input() maxDate: Date;
  weekendFilter = (d: Date) => true;

  constructor(
    private configStore: ConfigStoreService,
    private dateAdapter: DateAdapter<any>,
    private localeService: LocaleService) {
    this.dateFormat = configStore.ngxDrpOptions.format;
    if (configStore.ngxDrpOptions.excludeWeekends) {
      this.weekendFilter = (d: Date): boolean => {
        const day = d.getDay();
        return day !== 0 && day !== 6;
      };
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.selectedDate !== undefined) {
      this.matCalendar.activeDate = changes.selectedDate.currentValue;
    }
    this.dateAdapter.setLocale(this.localeService.getLocale());
  }

  onSelectedChange(date) {
    this.selectedDateChange.emit(date);
  }

  onYearSelected(e) {}

  onUserSelection(e) {}

}
