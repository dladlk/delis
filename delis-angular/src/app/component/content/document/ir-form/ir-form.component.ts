import { Component, ElementRef, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { MatCheckboxChange, MatSelect } from '@angular/material';
import { SuccessModel } from '../../../../model/system/success.model';
import { DocumentInvoiceModel } from '../../../../model/content/document/document-invoice.model';
import { DocumentInvoiceResponseFormModel } from '../../../../model/content/document/document-invoice-response-form.model';
import { InvoiceResponseGenerationModel } from '../../../../model/content/document/invoice-response-generation.model';
import { ErrorModel } from '../../../../model/system/error.model';
import { InvoiceErrorRecordModel } from '../../../../model/content/document/invoice-error-record.model';
import { DocumentInvoiceService } from '../../../../service/content/document-invoice.service';
import { ErrorService } from '../../../../service/system/error.service';
import { FileSaverService } from '../../../../service/system/file-saver.service';
import { SpinnerService } from '../../../../service/system/spinner.service';
import { DelisEntityDetailsObservable } from '../../../../observable/delis-entity-details.observable';

@Component({
  selector: 'app-ir-form',
  templateUrl: './ir-form.component.html',
  styleUrls: ['./ir-form.component.scss']
})
export class IrFormComponent implements OnInit, OnDestroy {

  @Input() documentId: number;
  @Input() effectiveDate: string;

  @ViewChild('inputGroupStatusReason', {static: true}) inputGroupStatusReason: MatSelect;
  @ViewChild('inputGroupEffectiveDate', {static: true}) inputGroupEffectiveDate: ElementRef;
  @ViewChild('inputGroupStatusAction', {static: true}) inputGroupStatusAction: MatSelect;
  @ViewChild('inputGroupStatusAction2', {static: true}) inputGroupStatusAction2: MatSelect;

  error = false;
  loading = false;
  success: SuccessModel;
  documentInvoiceModel: DocumentInvoiceModel = new DocumentInvoiceModel();
  documentInvoiceResponseFormModel: DocumentInvoiceResponseFormModel = new DocumentInvoiceResponseFormModel();
  invoiceResponseGenerationModel: InvoiceResponseGenerationModel = new InvoiceResponseGenerationModel();

  invoiceResponseUseCaseView: string[] = [];
  invoiceStatusCodeView: string[] = [];
  statusReasonView: string[] = [];
  statusActionView: string[] = [];
  statusAction2View: string[] = [];
  statusReasonText: string;
  detailType: string;
  detailValue: string;

  validateGenerated = 'Validate generated';
  onlyGenerated = 'Only generate, do not send';

  effectiveDateEnabled = false;

  statusReasonEnabled = false;
  statusActionEnabled = false;
  statusAction2Enabled = false;
  validateGeneratedEnabled = true;
  onlyGeneratedEnabled = true;

  errorDownload = false;
  errorDownloadModel: ErrorModel;

  errorList: InvoiceErrorRecordModel[] = [];
  isErrorList = false;

  private pageUpdate$: Subscription;

  constructor(private documentInvoiceService: DocumentInvoiceService,
              private errorService: ErrorService,
              public spinnerService: SpinnerService,
              private delisEntityDetailsObservable: DelisEntityDetailsObservable) {
    this.pageUpdate$ = this.delisEntityDetailsObservable.listen().subscribe((id: any) => {
      this.documentId = id;
      this.loadInvoice(this.documentId);
    });
  }

  ngOnInit() {
    if (this.documentId) {
      this.loadInvoice(this.documentId);
    }
  }
  ngOnDestroy() {
    if (this.pageUpdate$) {
      this.pageUpdate$.unsubscribe();
    }
  }

  loadInvoice(id: any) {
    this.documentInvoiceService.getInvoice(id).subscribe((data: any) => {
      this.error = false;
      this.errorDownload = false;
      this.isErrorList = false;
      this.documentInvoiceModel = data.data;
      this.setDefaultConfig();
    }, error => {
      this.errorService.errorProcess(error);
      this.error = true;
    });
  }

  setDefaultConfig() {
    this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList[0];
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
  }

  initUseCase(useCase: string) {
    this.documentInvoiceResponseFormModel.usecase = useCase;
  }

  selectInvoiceResponseUseCaseView(value: string) {
    if (value !== 'Select use case') {
      const useCaseId = value[0];
      switch (useCaseId) {
        case '1': {
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isIP(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '2a': {
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isIP(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '2a')[0];
              this.effectiveDateEnabled = true;
              this.statusActionEnabled = true;
              this.detailType = 'Buyer process reference';
              this.detailValue = 'X001';
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '2b': {
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isIP(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '2b')[0];
              this.statusReasonText = 'Shipment has not yet been received. Invoice processing will be attempted later.';
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '3': {
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isAP(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '4a': {
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isRE(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.statusReasonText = 'A textual explanation for why the invoice is being rejected.';
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '4b': {
          // tslint:disable-next-line:forin
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isRE(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === 'REF')[0];
              this.statusActionView = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === 'NIN')[0];
              this.statusReasonEnabled = true;
              this.statusActionEnabled = true;
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '4c': {
          // tslint:disable-next-line:forin
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isRE(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === 'REF')[0];
              this.statusActionView = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === 'CNF')[0];
              this.statusAction2View = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === 'NIN')[0];
              this.statusReasonEnabled = true;
              this.statusActionEnabled = true;
              this.statusAction2Enabled = true;
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '5': {
          // tslint:disable-next-line:forin
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isCA(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '5')[0];
              this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === 'PAY')[0];
              this.statusReasonEnabled = true;
              this.detailType = 'BT-9';
              this.detailValue = '2018-01-15';
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '6a': {
          // tslint:disable-next-line:forin
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
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
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '6b': {
          // tslint:disable-next-line:forin
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
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
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '6c': {
          // tslint:disable-next-line:forin
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isUQ(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '6c')[0];
              this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === 'DEL')[0];
              this.statusActionView = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === 'CNP')[0];
              this.statusReasonEnabled = true;
              this.effectiveDateEnabled = true;
              this.statusActionEnabled = true;
              this.statusReasonText = 'Delivered quantity for line number 1 was 2 units but invoiced quantity is 5 units. Send credit note for 3 unit.';
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '7': {
          // tslint:disable-next-line:forin
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isPD(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '7')[0];
              this.effectiveDateEnabled = true;
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
        case '8': {
          // tslint:disable-next-line:forin
          for (const uCase in this.documentInvoiceModel.invoiceStatusCodeList) {
            if (this.isAP(uCase)) {
              this.setDefaultConfig();
              this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList[uCase];
              this.invoiceResponseUseCaseView = this.documentInvoiceModel.invoiceResponseUseCaseList.filter(uc => uc[0] === '8')[0];
              this.initUseCase(useCaseId);
            }
          }
          break;
        }
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
    const statusId = value[0];
    this.invoiceStatusCodeView = this.documentInvoiceModel.invoiceStatusCodeList.filter(uc => uc[0] === statusId)[0];
  }

  checkEffectiveDateEnabled() {
    this.focusElement(this.inputGroupEffectiveDate, this.effectiveDateEnabled);
  }

  selectStatusReasonView(value: any) {
    const reasonId = value[0];
    this.statusReasonView = this.documentInvoiceModel.statusReasonList.filter(sr => sr[0] === reasonId)[0];
  }

  checkStatusReasonEnabled(event: MatCheckboxChange) {
    this.focusSelectElement(this.inputGroupStatusReason, this.statusReasonEnabled, event);
  }

  selectStatusActionView(value: any) {
    const actionId = value[0];
    this.statusActionView = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === actionId)[0];
  }

  checkStatusActionEnabled(event: any) {
    this.focusSelectElement(this.inputGroupStatusAction, this.statusActionEnabled, event);
  }

  selectStatusAction2View(value: any) {
    const action2Id = value[0];
    this.statusAction2View = this.documentInvoiceModel.statusActionList.filter(sr => sr[0] === action2Id)[0];
  }

  checkStatusAction2Enabled(event: any) {
    this.focusSelectElement(this.inputGroupStatusAction2, this.statusAction2Enabled, event);
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
      }, 0);
    }
  }

  focusSelectElement(element: MatSelect, focus: boolean, event: MatCheckboxChange) {
    setTimeout(() => {
      if (focus && event.checked) {
        element.open();
      } else {
        element.close();
        element.disabled = true;
      }
    }, 0);
  }

  sendInvoice(element: any) {
    this.sendProcess(element);
  }

  sendProcess(element: any) {

    this.documentInvoiceResponseFormModel.documentId = this.documentId;
    this.documentInvoiceResponseFormModel.generateWithoutSending = this.onlyGeneratedEnabled;
    this.documentInvoiceResponseFormModel.validate = this.validateGeneratedEnabled;

    this.invoiceResponseGenerationModel.status = this.invoiceStatusCodeView[0];
    this.invoiceResponseGenerationModel.effectiveDate = this.effectiveDate;
    this.invoiceResponseGenerationModel.effectiveDateEnabled = this.effectiveDateEnabled;

    this.invoiceResponseGenerationModel.reasonEnabled = this.statusReasonEnabled;
    if (this.statusReasonEnabled) {
      this.invoiceResponseGenerationModel.reason = this.statusReasonView[0];
    }
    this.invoiceResponseGenerationModel.actionEnabled = this.statusActionEnabled;
    if (this.statusActionEnabled) {
      this.invoiceResponseGenerationModel.action = this.statusActionView[0];
    }
    this.invoiceResponseGenerationModel.action2Enabled = this.statusAction2Enabled;
    if (this.statusAction2Enabled) {
      this.invoiceResponseGenerationModel.action2 = this.statusAction2View[0];
    }

    this.invoiceResponseGenerationModel.statusReasonText = this.statusReasonText;
    this.invoiceResponseGenerationModel.detailType = this.detailType;
    this.invoiceResponseGenerationModel.detailValue = this.detailValue;

    this.documentInvoiceResponseFormModel.data = this.invoiceResponseGenerationModel;

    this.errorDownload = false;
    this.isErrorList = false;
    this.success = undefined;

    if (this.onlyGeneratedEnabled) {
      // getFile
      this.documentInvoiceService.generateAndDownloadFileByDocument(this.documentInvoiceResponseFormModel).subscribe(response => {
          const filename = FileSaverService.getFileNameFromResponseContentDisposition(response);
          FileSaverService.saveFile(response.body, filename);
        },
        (error) => {
          this.errorDownloadModel = this.errorService.errorProcess(error);
          this.errorDownload = true;
          if (this.errorDownloadModel.details !== null) {
            this.errorList = this.errorDownloadModel.details;
            this.isErrorList = true;
          }
        }
      );
    } else {
      // easy post
      this.documentInvoiceService.generateAndSend(this.documentInvoiceResponseFormModel).subscribe((data: any) => {
          this.success = data.data;
        }, (error) => {
          this.errorDownloadModel = this.errorService.errorProcess(error);
          this.errorDownload = true;
          if (this.errorDownloadModel.details !== null) {
            this.errorList = this.errorDownloadModel.details;
            this.isErrorList = true;
          }
        }
      );
    }

    element.scrollIntoView({ behavior: 'smooth', block: 'end' });
  }

}
