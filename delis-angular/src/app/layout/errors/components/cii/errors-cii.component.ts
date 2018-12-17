import { Component, OnInit } from '@angular/core';
import { routerTransition } from '../../../../router.animations';
import { ErrorsCiiService } from '../../services/errors-cii.service';

@Component({
  selector: 'app-errors-cii',
  templateUrl: './errors-cii.component.html',
  styleUrls: ['./errors-cii.component.scss'],
  animations: [routerTransition()]
})
export class ErrorsCiiComponent implements OnInit {

  constructor(private err: ErrorsCiiService) {}

  ngOnInit() {}

  getErrors() {
    return this.err.getErrors();
  }
}
