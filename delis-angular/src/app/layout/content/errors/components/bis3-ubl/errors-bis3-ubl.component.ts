import { Component, OnInit } from '@angular/core';
import { routerTransition } from '../../../../../router.animations';
import { ErrorsBis3UblService } from '../../services/errors-bis3-ubl.service';

@Component({
  selector: 'app-errors-bis3-ubl',
  templateUrl: './errors-bis3-ubl.component.html',
  styleUrls: ['./errors-bis3-ubl.component.scss'],
  animations: [routerTransition()]
})
export class ErrorsBis3UblComponent implements OnInit {

  constructor(private err: ErrorsBis3UblService) {}

  ngOnInit() {}

  getErrors() {
    return this.err.getErrors();
  }
}
