import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { FileSaverService } from '../../../../service/system/file-saver.service';
import { SendDocumentModel } from '../../../../model/content/send-document/send-document.model';
import { SendDocumentsBytesModel } from '../../../../model/content/send-document/send-documents-bytes.model';
import { JournalSendDocumentModel } from '../../../../model/content/send-document/journal-send-document.model';
import { ErrorModel } from '../../../../model/system/error.model';
import { LocaleService } from '../../../../service/system/locale.service';
import { ErrorService } from '../../../../service/system/error.service';
import { SendDocumentService } from '../../../../service/content/send-document.service';

import { SHOW_DATE_FORMAT, SEND_DOCUMENT_PATH } from '../../../../app.constants';

@Component({
  selector: 'app-send-document-details',
  templateUrl: './send-document-details.component.html',
  styleUrls: ['./send-document-details.component.scss']
})
export class SendDocumentDetailsComponent implements OnInit {

  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

  sendDocument: SendDocumentModel = new SendDocumentModel();
  sendDocumentsBytes: SendDocumentsBytesModel[] = [];
  journalSendDocuments: JournalSendDocumentModel[] = [];

  errorOneDocument = false;
  errorOneDocumentModel: ErrorModel;

  errorDocumentBytes = false;
  errorDocumentBytesModel: ErrorModel;

  errorJournalDocuments = false;
  errorJournalDocumentsModel: ErrorModel;

  errorDownload = false;
  errorDownloadModel: ErrorModel;

  documentId: number;

  constructor(
    private location: Location,
    private translate: TranslateService,
    private locale: LocaleService,
    private route: ActivatedRoute,
    private router: Router,
    private errorService: ErrorService,
    private sendDocumentService: SendDocumentService) { }

  ngOnInit() {
    const id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
    this.documentId = id;
    this.sendDocumentService.getOneSendDocumentsById(id).subscribe((data: SendDocumentModel) => {
      this.sendDocument = data;
      this.errorOneDocument = false;
    }, error => {
      this.errorOneDocumentModel = this.errorService.errorProcess(error);
      this.errorOneDocument = true;
    });
    this.sendDocumentService.getListSendDocumentBytesBySendDocumentId(id).subscribe((data: {}) => {
      this.sendDocumentsBytes = data['items'];
      console.log(data);
      this.errorDocumentBytes = false;
    }, error => {
      this.errorDocumentBytesModel = this.errorService.errorProcess(error);
      this.errorDocumentBytes = true;
    });
    this.sendDocumentService.getListJournalSendDocumentBySendDocumentId(id).subscribe(
      (data: {}) => {
        this.journalSendDocuments = data['items'];
        this.errorJournalDocuments = false;
      }, error => {
        this.errorJournalDocumentsModel = this.errorService.errorProcess(error);
        this.errorJournalDocuments = true;
      }
    );
  }

  download(id: number) {
    this.sendDocumentService.downloadFileBySendDocumentAndDocumentBytes(this.sendDocument.id, id).subscribe(response => {
        const filename = FileSaverService.getFileNameFromResponseContentDisposition(response);
        FileSaverService.saveFile(response.body, filename);
        this.errorDownload = false;
      },
      error => {
        this.errorDownloadModel = this.errorService.errorProcess(error);
        this.errorDownload = true;
      }
    );
  }

  back() {
    this.router.navigate(['/' + SEND_DOCUMENT_PATH], { queryParams: { skip: false } });
    this.location.back();
  }

  refreshData() {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
      this.router.navigate(['/' + SEND_DOCUMENT_PATH, this.documentId]));
  }

  isReceipt(type: string) {
    if(this.locale.getLocale() === 'da') {
      return type === 'Kvittering';
    } else {
      return type === 'Receipt';
    }
  }
}
