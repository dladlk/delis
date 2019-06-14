import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {routerTransition} from '../../../../../router.animations';
import {DocumentInvoiceService} from '../../services/document.invoice.service';
import {DocumentModel} from '../../models/document.model';
import {ErrorService} from '../../../../../service/error.service';
import {DocumentInvoiceModel} from '../../models/document.invoice.model';

@Component({
    selector: 'app-document-invoice',
    templateUrl: './document.invoice.component.html',
    styleUrls: ['./document.invoice.component.scss'],
    animations: [routerTransition()]
})
export class DocumentInvoiceComponent implements OnInit {

    error = false;
    documentInvoiceModel: DocumentInvoiceModel = new DocumentInvoiceModel();

    invoiceStatusCodeView: string;
    invoiceResponseUseCaseView: string;

    effectiveDateEnabled: any;
    effectiveDate: any;

    constructor(private route: ActivatedRoute, private documentInvoiceService: DocumentInvoiceService, private errorService: ErrorService) {
    }

    ngOnInit(): void {
        // tslint:disable-next-line:radix
        const id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.documentInvoiceService.getInvoice(id).subscribe((data: DocumentModel) => {
            this.documentInvoiceModel = data['data'];
            console.log(this.documentInvoiceModel);
            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[0];
        }, error => {
            this.errorService.errorProcess(error);
            this.error = true;
        });
    }

    selectInvoiceResponseUseCaseView(value: any) {
        console.log(value);
        console.log(this.invoiceResponseUseCaseView);
    }

    selectInvoiceStatusCodeView(value: any) {
        console.log(value);
        console.log(this.invoiceStatusCodeView);
    }

    checkboxChanged(thisValue: any, boolValue: boolean) {
        console.log(thisValue);
        console.log(boolValue);
    }
}
