import { Component, OnInit, Input } from '@angular/core';
import { ErrorsModel } from '../../../layout/content/errors/models/ErrorsModel';

@Component({
  selector: 'app-errors',
  templateUrl: './errors.component.html',
  styleUrls: ['./errors.component.scss']
})
export class ErrorsComponent implements OnInit {

  @Input() title: string;
  @Input() errors: Array<ErrorsModel> = [];

  constructor() {}

  ngOnInit() {}

  getErrors() {
    return this.errors;
  }
}
