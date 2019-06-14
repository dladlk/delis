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
    statusReasonView: string;
    statusActionView: string;
    statusAction2View: string;

    effectiveDateEnabled = false;
    effectiveDate: Date;

    statusReasonEnabled = false;
    statusActionEnabled = false;
    statusAction2Enabled = false;

    constructor(private route: ActivatedRoute, private documentInvoiceService: DocumentInvoiceService, private errorService: ErrorService) {
    }

    ngOnInit(): void {
        // tslint:disable-next-line:radix
        const id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.documentInvoiceService.getInvoice(id).subscribe((data: DocumentModel) => {
            this.documentInvoiceModel = data['data'];
            console.log(this.documentInvoiceModel);
            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[0];
            this.statusReasonView = this.documentInvoiceModel.statusReasonList[0];
            this.statusActionView = this.documentInvoiceModel.statusActionList[0];
            this.statusAction2View = this.documentInvoiceModel.statusActionList[0];
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

    checkEffectiveDateEnabled(thisValue: any, boolValue: boolean) {
        console.log(thisValue);
        console.log(boolValue);
        this.effectiveDateEnabled = boolValue;
        console.log(this.effectiveDate);
        return !this.effectiveDateEnabled;
    }

    selectStatusReasonView(value: any) {
        console.log(value);
        console.log(this.statusReasonView);
    }

    checkStatusReasonEnabled(thisValue: any, boolValue: boolean) {
        console.log(thisValue);
        console.log(boolValue);
        this.statusReasonEnabled = boolValue;
        return !this.statusReasonEnabled;
    }

    selectStatusActionView(value: any) {
        console.log(value);
        console.log(this.statusActionView);
    }

    checkStatusActionEnabled(thisValue: any, boolValue: boolean) {
        console.log(thisValue);
        console.log(boolValue);
        this.statusActionEnabled = boolValue;
        return !this.statusActionEnabled;
    }

    selectStatusAction2View(value: any) {
        console.log(value);
        console.log(this.statusAction2View);
    }

    checkStatusAction2Enabled(thisValue: any, boolValue: boolean) {
        console.log(thisValue);
        console.log(boolValue);
        this.statusAction2Enabled = boolValue;
        return !this.statusAction2Enabled;
    }
}
