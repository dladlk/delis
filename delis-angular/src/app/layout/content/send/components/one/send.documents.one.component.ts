import {Component, OnInit} from '@angular/core';
import {routerTransition} from '../../../../../router.animations';
import {HeaderModel} from '../../../../components/header/header.model';
import {SHOW_DATE_FORMAT} from '../../../../../app.constants';
import {SendDocumentsModel} from '../../models/send.documents.model';
import {SendDocumentsBytesModel} from '../../models/send.documents.bytes.model';
import {JournalSendDocumentModel} from '../../models/journal.send.document.model';
import {TranslateService} from '@ngx-translate/core';
import {LocaleService} from '../../../../../service/locale.service';
import {ActivatedRoute} from '@angular/router';
import {ErrorService} from '../../../../../service/error.service';
import {SendDocumentsService} from '../../service/send.documents.service';
import {FileSaverService} from '../../../../../service/file.saver.service';
import {ErrorModel} from '../../../../../models/error.model';

@Component({
    selector: 'app-documents-send-one',
    templateUrl: './send.documents.one.component.html',
    styleUrls: ['./send.documents.one.component.scss'],
    animations: [routerTransition()]
})
export class SendDocumentsOneComponent implements OnInit {

    pageHeaders: HeaderModel[] = [];
    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    sendDocument: SendDocumentsModel = new SendDocumentsModel();
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

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private sendDocumentsService: SendDocumentsService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            {routerLink: '/send-documents', heading: 'documents.send.header', icon: 'fa fa-book'}
        );
    }

    ngOnInit(): void {
        const id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.sendDocumentsService.getOneSendDocumentsById(id).subscribe((data: SendDocumentsModel) => {
            this.sendDocument = data;
            this.errorOneDocument = false;
        }, error => {
            this.errorOneDocumentModel = this.errorService.errorProcess(error);
            this.errorOneDocument = true;
        });
        this.sendDocumentsService.getListSendDocumentBytesBySendDocumentId(id).subscribe((data: {}) => {
            this.sendDocumentsBytes = data['items'];
            this.errorDocumentBytes = false;
        }, error => {
            this.errorDocumentBytesModel = this.errorService.errorProcess(error);
            this.errorDocumentBytes = true;
        });
        this.sendDocumentsService.getListJournalSendDocumentBySendDocumentId(id).subscribe(
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
        this.sendDocumentsService.downloadFileBySendDocumentAndDocumentBytes(this.sendDocument.id, id).subscribe(response => {
                const filename = response.headers.get('filename');
                FileSaverService.saveFile(response.body, filename);
                this.errorDownload = false;
            },
            error => {
                this.errorDownloadModel = this.errorService.errorProcess(error);
                this.errorDownload = true;
            }
        );
    }
}
