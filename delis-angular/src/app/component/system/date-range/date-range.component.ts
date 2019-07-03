import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { NgxDrpOptions, PresetItem, Range } from './model/model';

@Component({
  selector: 'app-date-range',
  templateUrl: './date-range.component.html',
  styleUrls: ['./date-range.component.scss']
})
export class DateRangeComponent implements OnInit {

  @ViewChild('dateRangePicker', {static: true}) dateRangePicker;
  @Input() placeholder: string;

  range: Range = {fromDate: new Date(), toDate: new Date()};
  options: NgxDrpOptions;
  presets: Array<PresetItem> = [];

  ngOnInit() {
    this.setupPresets();
    this.options = {
      presets: this.presets,
      format: 'mediumDate',
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

    const backDate = (numOfDays) => {
      const today = new Date();
      return new Date(today.setDate(today.getDate() - numOfDays));
    };

    const today = new Date();
    const todayStart = new Date();
    todayStart.setHours(0,0,0,0)
    const yesterday = backDate(1);
    const minus7 = backDate(7);
    const minus30 = backDate(30);
    const currMonthStart = new Date(today.getFullYear(), today.getMonth(), 1);
    const currMonthEnd = new Date(today.getFullYear(), today.getMonth() + 1, 0);
    const lastMonthStart = new Date(today.getFullYear(), today.getMonth() - 1, 1);
    const lastMonthEnd = new Date(today.getFullYear(), today.getMonth(), 0);

    this.presets =  [
      {presetLabel: 'td.picker.today', range: { fromDate: todayStart, toDate: today }},
      {presetLabel: 'td.picker.yesterday', range: { fromDate: yesterday, toDate: today }},
      {presetLabel: 'td.picker.last7Days', range: { fromDate: minus7, toDate: today }},
      {presetLabel: 'td.picker.last30Days', range: { fromDate: minus30, toDate: today }},
      {presetLabel: 'td.picker.thisMonth', range: { fromDate: currMonthStart, toDate: currMonthEnd }},
      {presetLabel: 'td.picker.lastMonth', range: { fromDate: lastMonthStart, toDate: lastMonthEnd }}
    ];
  }
}
