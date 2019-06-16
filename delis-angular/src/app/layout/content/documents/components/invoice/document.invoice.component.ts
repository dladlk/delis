import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {routerTransition} from '../../../../../router.animations';
import {DocumentInvoiceService} from '../../services/document.invoice.service';
import {DocumentModel} from '../../models/document.model';
import {ErrorService} from '../../../../../service/error.service';
import {DocumentInvoiceModel} from '../../models/document.invoice.model';

const BORDER_COLOR_GREY = '#ced4da';
const BORDER_COLOR_GREEN = '#28a745';

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

    constructor(private route: ActivatedRoute, private documentInvoiceService: DocumentInvoiceService, private errorService: ErrorService) {

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
        this.invoiceResponseUseCaseView = [];
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
        this.effectiveDateEnabled = false;
        document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREY;
        document.getElementById('inputGroupStatusAction').style.borderColor = BORDER_COLOR_GREY;
        document.getElementById('inputGroupStatusAction2').style.borderColor = BORDER_COLOR_GREY;
        document.getElementById('inputGroupStatusReason').style.borderColor = BORDER_COLOR_GREY;
        document.getElementById('invoiceResponseDetailTypeCode').style.borderColor = BORDER_COLOR_GREY;
        document.getElementById('invoiceResponseDetailValue').style.borderColor = BORDER_COLOR_GREY;
        document.getElementById('statusReasonText').style.borderColor = BORDER_COLOR_GREY;
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
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
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
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusAction').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('invoiceResponseDetailTypeCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('invoiceResponseDetailValue').style.borderColor = BORDER_COLOR_GREEN;
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
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('statusReasonText').style.borderColor = BORDER_COLOR_GREEN;
                            this.statusReasonText = 'Shipment has not yet been received. Invoice processing will be attempted later.';
                        }
                    }
                } break;
                case '3': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isAP(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                        }
                    }
                } break;
                case '4a': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isRE(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('statusReasonText').style.borderColor = BORDER_COLOR_GREEN;
                            this.statusReasonText = 'A textual explanation for why the invoice is being rejected.';
                        }
                    }
                } break;
                case '4b': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isRE(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === 'REF')[0];
                            this.statusActionView = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === 'NIN')[0];
                            this.statusReasonEnabled = true;
                            this.statusActionEnabled = true;
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusAction').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusReason').style.borderColor = BORDER_COLOR_GREEN;
                        }
                    }
                } break;
                case '4c': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isRE(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === 'REF')[0];
                            this.statusActionView = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === 'CNF')[0];
                            this.statusAction2View = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === 'NIN')[0];
                            this.statusReasonEnabled = true;
                            this.statusActionEnabled = true;
                            this.statusAction2Enabled = true;
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusAction').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusAction2').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusReason').style.borderColor = BORDER_COLOR_GREEN;
                        }
                    }
                } break;
                case '5': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isCA(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '5')[0];
                            this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === 'PAY')[0];
                            this.statusReasonEnabled = true;
                            this.detailType = 'BT-9';
                            this.detailValue = '2018-01-15';
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusReason').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('invoiceResponseDetailTypeCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('invoiceResponseDetailValue').style.borderColor = BORDER_COLOR_GREEN;
                        }
                    }
                } break;
                case '6a': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isUQ(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '6a')[0];
                            this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === 'REF')[0];
                            this.statusActionView = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === 'PIN')[0];
                            this.statusReasonEnabled = true;
                            this.effectiveDateEnabled = true;
                            this.statusActionEnabled = true;
                            this.detailType = 'BT-13';
                            this.detailValue = 'PO0001';
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusReason').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('invoiceResponseDetailTypeCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('invoiceResponseDetailValue').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusAction').style.borderColor = BORDER_COLOR_GREEN;
                        }
                    }
                } break;
                case '6b': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isUQ(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '6b')[0];
                            this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === 'REF')[0];
                            this.statusActionView = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === 'PIN')[0];
                            this.statusReasonEnabled = true;
                            this.effectiveDateEnabled = true;
                            this.statusActionEnabled = true;
                            this.detailType = 'BT-13';
                            this.detailValue = 'PO0001';
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusReason').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('invoiceResponseDetailTypeCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('invoiceResponseDetailValue').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusAction').style.borderColor = BORDER_COLOR_GREEN;
                        }
                    }
                } break;
                case '6c': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isUQ(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '6c')[0];
                            this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === 'DEL')[0];
                            this.statusActionView = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === 'CNP')[0];
                            this.statusReasonEnabled = true;
                            this.effectiveDateEnabled = true;
                            this.statusActionEnabled = true;
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusReason').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('inputGroupStatusAction').style.borderColor = BORDER_COLOR_GREEN;
                            document.getElementById('statusReasonText').style.borderColor = BORDER_COLOR_GREEN;
                            this.statusReasonText = 'Delivered quantity for line number 1 was 2 units but invoiced quantity is 5 units. Send credit note for 3 unit.';
                        }
                    }
                } break;
                case '7': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isPD(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '7')[0];
                            this.effectiveDateEnabled = true;
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                        }
                    }
                } break;
                case '8': {
                    // tslint:disable-next-line:forin
                    for (let uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
                        if (this.isAP(uCase)) {
                            this.setDefaultConfig();
                            this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
                            this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '8')[0];
                            document.getElementById('inputGroupStatusCode').style.borderColor = BORDER_COLOR_GREEN;
                        }
                    }
                } break;
                default : {
                    this.setDefaultConfig();
                }
            }
        } else {
            this.setDefaultConfig();
        }
    }

    isIP(uCase: any) {
        return this.documentInvoiceModel.invoiceStatusCodeList[uCase][0] === 'IP';
    }

    isAP(uCase: any) {
        return this.documentInvoiceModel.invoiceStatusCodeList[uCase][0] === 'AP';
    }

    isRE(uCase: any) {
        return this.documentInvoiceModel.invoiceStatusCodeList[uCase][0] === 'RE';
    }

    isCA(uCase: any) {
        return this.documentInvoiceModel.invoiceStatusCodeList[uCase][0] === 'CA';
    }

    isUQ(uCase: any) {
        return this.documentInvoiceModel.invoiceStatusCodeList[uCase][0] === 'UQ';
    }

    isPD(uCase: any) {
        return this.documentInvoiceModel.invoiceStatusCodeList[uCase][0] === 'PD';
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
