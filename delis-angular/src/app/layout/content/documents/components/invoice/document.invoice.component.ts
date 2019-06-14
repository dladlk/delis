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
    statusReasonText: string;
    detailType: string;
    detailValue: string;

    validateGenerated = "Validate generated";
    onlyGenerated = "Only generate, do not send";

    effectiveDateEnabled = false;
    effectiveDate: Date;

    statusReasonEnabled = false;
    statusActionEnabled = false;
    statusAction2Enabled = false;
    validateGeneratedEnabled = true;
    onlyGeneratedEnabled = true;

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

    checkEffectiveDateEnabled() {
        this.effectiveDateEnabled = !this.effectiveDateEnabled;
        return this.effectiveDateEnabled;
    }

    selectStatusReasonView(value: any) {
        console.log(value);
        console.log(this.statusReasonView);
    }

    checkStatusReasonEnabled() {
        this.statusReasonEnabled = !this.statusReasonEnabled;
        return this.statusReasonEnabled;
    }

    selectStatusActionView(value: any) {
        console.log(value);
        console.log(this.statusActionView);
    }

    checkStatusActionEnabled() {
        this.statusActionEnabled = !this.statusActionEnabled;
        return this.statusActionEnabled;
    }

    selectStatusAction2View(value: any) {
        console.log(value);
        console.log(this.statusAction2View);
    }

    checkStatusAction2Enabled() {
        this.statusAction2Enabled = !this.statusAction2Enabled;
        return this.statusAction2Enabled;
    }

    checkValidateGeneratedEnabled() {
        this.validateGeneratedEnabled = !this.validateGeneratedEnabled;
        return this.validateGeneratedEnabled;
    }

    checkOnlyGeneratedEnabled() {
        this.onlyGeneratedEnabled = !this.onlyGeneratedEnabled;
        return this.onlyGeneratedEnabled;
    }
}
