import {Component, OnInit} from "@angular/core";
import {routerTransition} from "../../../../../router.animations";
import {HeaderModel} from "../../../../components/header/header.model";
import {SHOW_DATE_FORMAT} from "../../../../../app.constants";
import {SendDocumentsModel} from "../../models/send.documents.model";
import {SendDocumentsBytesModel} from "../../models/send.documents.bytes.model";
import {JournalSendDocumentModel} from "../../models/journal.send.document.model";
import {TranslateService} from "@ngx-translate/core";
import {LocaleService} from "../../../../../service/locale.service";
import {ActivatedRoute} from "@angular/router";
import {ErrorService} from "../../../../../service/error.service";
import {SendDocumentsService} from "../../service/send.documents.service";
import {FileSaverService} from "../../../../../service/file.saver.service";

@Component({
    selector: 'app-documents-send-one',
    templateUrl: './send.documents.one.component.html',
    styleUrls: ['./send.documents.one.component.scss'],
    animations: [routerTransition()]
})
export class SendDocumentsOneComponent implements OnInit {

    pageHeaders: HeaderModel[] = [];
    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
    error = false;

    sendDocument: SendDocumentsModel = new SendDocumentsModel();
    sendDocumentsBytes: SendDocumentsBytesModel[] = [];
    journalSendDocuments: JournalSendDocumentModel[] = [];

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
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.sendDocumentsService.getOneSendDocumentsById(id).subscribe((data: SendDocumentsModel) => {
            this.sendDocument = data;
        }, error => {
            this.errorService.errorProcess(error);
            this.error = true;
        });
        this.sendDocumentsService.getListSendDocumentBytesBySendDocumentId(id).subscribe((data: {}) => {
            this.sendDocumentsBytes = data["items"];
        }, error => {
            this.errorService.errorProcess(error);
            this.error = true;
        });
        this.sendDocumentsService.getListJournalSendDocumentBySendDocumentId(id).subscribe(
            (data: {}) => {
                this.journalSendDocuments = data["items"];
            }, error => {
                this.errorService.errorProcess(error);
                this.error = true;
            }
        );
    }

    download(id: number) {
        this.sendDocumentsService.downloadFileBySendDocumentAndDocumentBytes(this.sendDocument.id, id).subscribe(response => {
                const filename = response.headers.get('filename');
                FileSaverService.saveFile(response.body, filename);
            },
            error => {
                this.errorService.errorProcess(error);
            }
        );
    }
}
