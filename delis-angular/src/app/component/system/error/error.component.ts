import { Component, Input, OnInit } from '@angular/core';
import { ErrorModel } from '../../../model/system/error.model';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss']
})
export class ErrorComponent implements OnInit {

  @Input() errorModel: ErrorModel;

  constructor() {}

  ngOnInit() { }
}
