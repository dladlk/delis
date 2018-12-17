import { Component, OnInit } from '@angular/core';
import { routerTransition } from '../../../../router.animations';
import { ErrorsInvoicesModel } from '../../models/ErrorsModel';

@Component({
  selector: 'app-errors-invoices',
  templateUrl: './errors-invoices.component.html',
  styleUrls: ['./errors-invoices.component.scss'],
  animations: [routerTransition()]
})
export class ErrorsInvoicesComponent implements OnInit {

  public errors: Array<ErrorsInvoicesModel> = [];

  constructor() {
    this.errors.push(
      {
        statusMessage: 'Received Error 22 CII',
        invoiceDate: new Date(1555621200),
        sender: 'Lego Italy',
        receiver: 'Region Midt',
        sendDate: new Date(1555707600)
      },
      {
        statusMessage: 'Received Error 12 UBL',
        invoiceDate: new Date(1555621200),
        sender: 'Lego Spain',
        receiver: 'Region Nord',
        sendDate: new Date(1555707600)
      },
      {
        statusMessage: 'Received Error 14 after conv.',
        invoiceDate: new Date(1555621200),
        sender: 'Lego Portugal',
        receiver: 'Region Midt',
        sendDate: new Date(1555707600)
      },
      {
        statusMessage: 'Received Error 25 after validat.',
        invoiceDate: new Date(1555621200),
        sender: 'Lego Italy',
        receiver: 'Region Nord',
        sendDate: new Date(1555707600)
      },
      {
        statusMessage: 'Received OIOUBL',
        invoiceDate: new Date(1555621200),
        sender: 'Lego Italy',
        receiver: 'Region Nord',
        sendDate: new Date(1555707600)
      }
    );
  }

  ngOnInit() {
  }
}
