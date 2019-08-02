import {
  Component,
  OnInit,
  Output,
  EventEmitter,
  Input,
  ChangeDetectionStrategy
} from '@angular/core';
import { PresetItem } from '../model/model';

@Component({
  selector: 'app-mat-drp-presets',
  templateUrl: './mat-drp-presets.component.html',
  styleUrls: ['./mat-drp-presets.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MatDrpPresetsComponent implements OnInit {

  @Input() presets: Array<PresetItem>;
  @Output() readonly presetChanged: EventEmitter<any> = new EventEmitter<any>();

  constructor() {}

  ngOnInit() {}

  setPresetPeriod(event) {
    this.presetChanged.emit(event);
  }
}
