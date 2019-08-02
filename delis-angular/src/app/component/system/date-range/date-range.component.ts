import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { NgxDrpOptions, PresetItem, Range } from './model/model';
import { DATE_FORMAT } from '../../../app.constants';

@Component({
  selector: 'app-date-range',
  templateUrl: './date-range.component.html',
  styleUrls: ['./date-range.component.scss']
})
export class DateRangeComponent implements OnInit {

  @ViewChild('dateRangePicker', {static: true}) dateRangePicker;
  @Input() placeholder: string;
  @Input() appearance: string;

  range: Range = {fromDate: null, toDate: null};
  options: NgxDrpOptions;
  presets: Array<PresetItem> = [];

  ngOnInit() {
    this.setupPresets();
    this.options = {
      presets: this.presets,
      format: DATE_FORMAT,
      range: null,
      placeholder: this.placeholder || 'Dato',
      applyLabel: 'Submit',
      calendarOverlayConfig: {
        shouldCloseOnBackdropClick: false,
        hasBackdrop: false
      }
    };
  }

  updateRange(range: Range) {
    this.range = range;
  }

  setupPresets() {
    const newDateToday = () => {
      return cleanDate(new Date());
    };
    const newDate = (year: number, month: number, day: number) => {
      return cleanDate(new Date(year, month, day));
    };
    const cleanDate = (nd: Date) => {
      //nd.setUTCHours(0,0,0,0);
      return nd;
    };    

    const backDate = (numOfDays) => {
      const today = newDateToday();
      return new Date(today.setDate(today.getDate() - numOfDays));
    };

    const today = newDateToday();
    const yesterday = backDate(1);
    const minus7 = backDate(7);
    const minus30 = backDate(30);
    const currMonthStart = newDate(today.getFullYear(), today.getMonth(), 1);
    const currMonthEnd = newDate(today.getFullYear(), today.getMonth() + 1, 0);
    const lastMonthStart = newDate(today.getFullYear(), today.getMonth() - 1, 1);
    const lastMonthEnd = newDate(today.getFullYear(), today.getMonth(), 0);

    this.presets =  [
      {presetLabel: 'td.picker.all', range: { fromDate: null, toDate: today }},
      {presetLabel: 'td.picker.today', range: { fromDate: today, toDate: today }},
      {presetLabel: 'td.picker.yesterday', range: { fromDate: yesterday, toDate: yesterday }},
      {presetLabel: 'td.picker.last7Days', range: { fromDate: minus7, toDate: today }},
      {presetLabel: 'td.picker.last30Days', range: { fromDate: minus30, toDate: today }},
      {presetLabel: 'td.picker.thisMonth', range: { fromDate: currMonthStart, toDate: currMonthEnd }},
      {presetLabel: 'td.picker.lastMonth', range: { fromDate: lastMonthStart, toDate: lastMonthEnd }}
    ];
  }
}
