import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
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

    @ViewChild('inputGroupStatusReason') inputGroupStatusReason: ElementRef;
    @ViewChild('inputGroupEffectiveDate') inputGroupEffectiveDate: ElementRef;
    @ViewChild('inputGroupStatusAction') inputGroupStatusAction: ElementRef;
    @ViewChild('inputGroupStatusAction2') inputGroupStatusAction2: ElementRef;

    error = false;
    documentInvoiceModel: DocumentInvoiceModel = new DocumentInvoiceModel();

    invoiceStatusCodeView: string[] = [];
    invoiceResponseUseCaseView: string[] = [];
    statusReasonView: string;
    statusActionView: string;
    statusAction2View: string;
    statusReasonText: string;
    detailType: string;
    detailValue: string;

    validateGenerated = 'Validate generated';
    onlyGenerated = 'Only generate, do not send';

    effectiveDateEnabled = false;
    effectiveDate: Date;

    statusReasonEnabled = false;
    statusActionEnabled = false;
    statusAction2Enabled = false;
    validateGeneratedEnabled = true;
    onlyGeneratedEnabled = true;

    useCaseConfig = {};

    constructor(private route: ActivatedRoute, private documentInvoiceService: DocumentInvoiceService, private errorService: ErrorService) {
        this.useCaseConfig['1'] = {'irs': 'IP'};
        this.useCaseConfig['2a'] = {'irs': 'IP', 'actionEnabled1': true, 'sca': 'NOA', 'detailType': 'Buyer process reference', 'detailValue': 'X001', 'effectiveDateEnabled1': true};
        this.useCaseConfig['2b'] = {'irs': 'IP', 'statusReasonText': 'Shipment has not yet been received. Invoice processing will be attempted later.', 'effectiveDateEnabled1': true};
        this.useCaseConfig['3'] = {'irs': 'AP'};
        this.useCaseConfig['4a'] = {'irs': 'RE', 'statusReasonText': 'A textual explanation for why the invoice is being rejected.'};
        this.useCaseConfig['4b'] = {'irs': 'RE', 'reasonEnabled1': true, 'scr': 'REF', 'actionEnabled1': true, 'sca': 'NIN'};
        this.useCaseConfig['4c'] = {'irs': 'RE', 'reasonEnabled1': true, 'scr': 'REF', 'actionEnabled1': true, 'sca': 'CNF', 'action2Enabled1': true, 'sca2': 'NIN'};
        this.useCaseConfig['5'] = {'irs': 'CA', 'reasonEnabled1': true, 'scr': 'PAY', 'detailType': 'BT-9', 'detailValue': '2018-01-15'};
        this.useCaseConfig['6a'] = {'irs': 'UQ', 'reasonEnabled1': true, 'scr': 'REF', 'detailType': 'BT-13', 'detailValue': 'PO0001', 'actionEnabled1': true, 'sca': 'PIN', 'effectiveDateEnabled1': true};
        this.useCaseConfig['6b'] = {'irs': 'UQ', 'reasonEnabled1': true, 'scr': 'REF', 'detailType': 'BT-13', 'detailValue': 'PO0001', 'actionEnabled1': true, 'sca': 'PIN', 'effectiveDateEnabled1': true};
        this.useCaseConfig['6c'] = {'irs': 'UQ', 'reasonEnabled1': true, 'scr': 'DEL', 'statusReasonText': 'Delivered quantity for line number 1 was 2 units but invoiced quantity is 5 units. Send credit note for 3 unit.', 'actionEnabled1': true, 'sca': 'CNP'};
        this.useCaseConfig['7'] = {'irs': 'PD', 'effectiveDateEnabled1': true};
        this.useCaseConfig['8'] = {'irs': 'AP'};

        this.useCaseConfig['mlr1'] = {'art': 'AB'};
        this.useCaseConfig['mlr2'] = {'art': 'RE', 'description': 'Rejected due to validation errors'};
        this.useCaseConfig['mlr3'] = {'art': 'RE', 'description': 'Rejected due to validation errors'};
    }

    ngOnInit(): void {
        // tslint:disable-next-line:radix
        const id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.documentInvoiceService.getInvoice(id).subscribe((data: DocumentModel) => {
            this.documentInvoiceModel = data['data'];
            console.log(this.documentInvoiceModel);
            this.setDefaultConfig();
        }, error => {
            this.errorService.errorProcess(error);
            this.error = true;
        });
    }

    setDefaultConfig() {
        this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[0];
        this.statusReasonView = this.documentInvoiceModel.statusReasonList[0];
        this.statusActionView = this.documentInvoiceModel.statusActionList[0];
        this.statusAction2View = this.documentInvoiceModel.statusActionList[0];
        this.statusReasonText = '';
        this.detailType = '';
        this.detailValue = '';
        this.statusReasonEnabled = false;
        this.statusActionEnabled = false;
        this.statusAction2Enabled = false;
        document.getElementById('inputGroupStatusCode').style.borderColor = '';
        document.getElementById('inputGroupStatusAction').style.borderColor = '';
        document.getElementById('invoiceResponseDetailTypeCode').style.borderColor = '';
        document.getElementById('invoiceResponseDetailValue').style.borderColor = '';
        document.getElementById('statusReasonText').style.borderColor = '';
    }

    selectInvoiceResponseUseCaseView(value: string) {
        console.log(value);
        if (value !== 'Select use case') {
            let useCase = value.split(':');
            let useCaseId = useCase[0];
            console.log(useCaseId);
            switch (useCaseId) {
                case '1': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isIP(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            document.getElementById('inputGroupStatusCode').style.borderColor = 'green';
                        }
                    }
                } break;
                case '2a': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isIP(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '2a')[0];
                            this.effectiveDateEnabled = true;
                            this.statusActionEnabled = true;
                            this.detailType = 'Buyer process reference';
                            this.detailValue = 'X001';
                            console.log(document.getElementById('inputGroupStatusCode').style.borderColor);
                            document.getElementById('inputGroupStatusCode').style.borderColor = 'green';
                            document.getElementById('inputGroupStatusAction').style.borderColor = 'green';
                            document.getElementById('invoiceResponseDetailTypeCode').style.borderColor = 'green';
                            document.getElementById('invoiceResponseDetailValue').style.borderColor = 'green';
                        }
                    }
                } break;
                case '2b': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isIP(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '2b')[0];
                            document.getElementById('inputGroupStatusCode').style.borderColor = 'green';
                            document.getElementById('statusReasonText').style.borderColor = 'green';
                            this.statusReasonText = 'Shipment has not yet been received. Invoice processing will be attempted later.';
                        }
                    }
                } break;
            }
        }
    }

    isIP(uCase: any) {
        return this.documentInvoiceModel.invoiceStatusCodeList[uCase][0] === 'IP';
    }

    selectInvoiceStatusCodeView(value: any) {
        console.log(value);
        console.log(this.invoiceStatusCodeView);
        console.log(this.invoiceResponseUseCaseView);
    }

    checkEffectiveDateEnabled() {
        this.effectiveDateEnabled = !this.effectiveDateEnabled;
        this.focusElement(this.inputGroupEffectiveDate, this.effectiveDateEnabled);
        return this.effectiveDateEnabled;
    }

    selectStatusReasonView(value: any) {
        console.log(value);
        console.log(this.statusReasonView);
    }

    checkStatusReasonEnabled() {
        this.statusReasonEnabled = !this.statusReasonEnabled;
        this.focusElement(this.inputGroupStatusReason, this.statusReasonEnabled);
        return this.statusReasonEnabled;
    }

    selectStatusActionView(value: any) {
        console.log(value);
        console.log(this.statusActionView);
    }

    checkStatusActionEnabled() {
        this.statusActionEnabled = !this.statusActionEnabled;
        this.focusElement(this.inputGroupStatusAction, this.statusActionEnabled);
        return this.statusActionEnabled;
    }

    selectStatusAction2View(value: any) {
        console.log(value);
        console.log(this.statusAction2View);
    }

    checkStatusAction2Enabled() {
        this.statusAction2Enabled = !this.statusAction2Enabled;
        this.focusElement(this.inputGroupStatusAction2, this.statusAction2Enabled);
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

    focusElement(element: ElementRef, focus: boolean) {
        if (focus) {
            setTimeout(() => {
                element.nativeElement.focus();
            },0);
        }
    }
}
