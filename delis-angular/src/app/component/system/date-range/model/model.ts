export interface PresetItem {
  presetLabel: string;
  range: Range;
}

export interface Range {
  fromDate: Date;
  toDate: Date;
}

export class RangeModel implements Range {
  fromDate: Date;
  toDate: Date;
}

export class RangeUpdate {
  range: Range;
  update: boolean;
}

export interface CalendarOverlayConfig {
  panelClass?: string;
  hasBackdrop?: boolean;
  backdropClass?: string;
  shouldCloseOnBackdropClick?: boolean;
}

export interface NgxDrpOptions {
  presets: Array<PresetItem>;
  format: string;
  range: Range;
  excludeWeekends?: boolean;
  locale?: string;
  fromMinMax?: Range;
  toMinMax?: Range;
  applyLabel?: string;
  cancelLabel?: string;
  animation?: boolean;
  calendarOverlayConfig?: CalendarOverlayConfig;
  placeholder?: string;
  startDatePrefix?: string;
  endDatePrefix?: string;
}
