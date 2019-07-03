import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import { OverlayRef } from '@angular/cdk/overlay';

import { PresetItem } from '../model/model';
import { RangeStoreService } from '../service/range-store.service';
import { ConfigStoreService } from '../service/config-store.service';
import { pickerOverlayAnimations } from './picker-overlay.animations';

@Component({
  selector: 'app-picker-overlay',
  templateUrl: './picker-overlay.component.html',
  styleUrls: ['./picker-overlay.component.scss'],
  animations: [pickerOverlayAnimations.transformPanel],
  encapsulation: ViewEncapsulation.None
})
export class PickerOverlayComponent implements OnInit {

  fromDate: Date;
  toDate: Date;
  fromMinDate: Date;
  fromMaxDate: Date;
  toMinDate: Date;
  toMaxDate: Date;
  presets: Array<PresetItem> = [];
  startDatePrefix: string;
  endDatePrefix: string;
  applyLabel: string;
  clearLabel: string;
  cancelLabel: string;
  shouldAnimate: string;

  constructor(
    private rangeStoreService: RangeStoreService,
    private configStoreService: ConfigStoreService,
    private overlayRef: OverlayRef
  ) {}

  ngOnInit() {
    this.fromDate = this.rangeStoreService.fromDate;
    this.toDate = this.rangeStoreService.toDate;
    this.startDatePrefix = this.configStoreService.ngxDrpOptions.startDatePrefix || 'td.picker.from';
    this.endDatePrefix = this.configStoreService.ngxDrpOptions.endDatePrefix || 'td.picker.to';
    this.applyLabel = this.configStoreService.ngxDrpOptions.applyLabel || 'Apply';
    this.clearLabel = this.configStoreService.ngxDrpOptions.cancelLabel || 'Clear';
    this.cancelLabel = this.configStoreService.ngxDrpOptions.cancelLabel || 'Cancel';
    this.presets = this.configStoreService.ngxDrpOptions.presets;
    this.shouldAnimate = this.configStoreService.ngxDrpOptions.animation
      ? 'enter'
      : 'noop';
    ({
      fromDate: this.fromMinDate,
      toDate: this.fromMaxDate
    } = this.configStoreService.ngxDrpOptions.fromMinMax);
    ({
      fromDate: this.toMinDate,
      toDate: this.toMaxDate
    } = this.configStoreService.ngxDrpOptions.toMinMax);
  }

  updateFromDate(date) {
    this.fromDate = date;
  }

  updateToDate(date) {
    this.toDate = date;
  }

  updateRangeByPreset(presetItem: PresetItem) {
    this.updateFromDate(presetItem.range.fromDate);
    this.updateToDate(presetItem.range.toDate);
  }

  applyNewDates(e) {
    this.rangeStoreService.updateRange(this.fromDate, this.toDate);
    this.disposeOverLay();
  }

  clearDates() {
    this.rangeStoreService.updateRange(null, null);
    this.disposeOverLay();
  }

  discardNewDates(e) {
    this.disposeOverLay();
  }

  private disposeOverLay() {
    this.overlayRef.dispose();
  }
}
