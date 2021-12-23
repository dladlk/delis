import { Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import { Location} from '@angular/common';
import { ActivatedRoute, Router} from '@angular/router';
import { TranslateService} from '@ngx-translate/core';
import { Subscription} from 'rxjs';

import { FileSaverService } from '../../../../service/system/file-saver.service';
import { SendDocumentStateService } from '../../../../service/state/send-document-state.service';
import { LocaleService } from '../../../../service/system/locale.service';
import { ErrorService } from '../../../../service/system/error.service';
import { SendDocumentService } from '../../../../service/content/send-document.service';
import { RoutingStateService } from '../../../../service/system/routing-state.service';
import { DelisEntityDetailsObservable } from '../../../../observable/delis-entity-details.observable';
import { SendDocumentModel } from '../../../../model/content/send-document/send-document.model';
import { SendDocumentsBytesModel } from '../../../../model/content/send-document/send-documents-bytes.model';
import { JournalSendDocumentModel } from '../../../../model/content/send-document/journal-send-document.model';
import { ErrorModel } from '../../../../model/system/error.model';

import { DASHBOARD_PATH, SEND_DOCUMENT_PATH, SHOW_DATE_FORMAT } from '../../../../app.constants';

@Component({
    selector: 'app-send-document-details',
    templateUrl: './send-document-details.component.html',
    styleUrls: ['./send-document-details.component.scss']
})
export class SendDocumentDetailsComponent implements OnInit, OnDestroy {

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
    SEND_DOCUMENT_PATH = SEND_DOCUMENT_PATH;

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

    isNextUp: boolean;
    isNextDown: boolean;
    currentIds: number[];

    isShowFooter: boolean;
    topPosToStartShowing = 100;

    private pageUpdate$: Subscription;

    @HostListener('window:scroll')
    checkScroll() {
        const scrollPosition = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
        this.isShowFooter = scrollPosition >= this.topPosToStartShowing;
    }

    constructor(
        private location: Location,
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private router: Router,
        private errorService: ErrorService,
        private sendDocumentService: SendDocumentService,
        public stateService: SendDocumentStateService,
        private routingState: RoutingStateService,
        private delisEntityDetailsObservable: DelisEntityDetailsObservable) {
        this.pageUpdate$ = this.delisEntityDetailsObservable.listen().subscribe((id: any) => {
            this.documentId = id;
            this.stateService.filter.detailsState.currentId = this.documentId;
            this.initPage(id);
            this.initStateDetails(id);
        });
    }

    ngOnInit() {
        if (this.stateService.getFilter() !== undefined) {
            this.documentId = this.stateService.getFilter().detailsState.currentId;
            this.initPage(this.documentId);
            this.initStateDetails(this.documentId);
        } else {
            this.router.navigate(['/' + DASHBOARD_PATH]);
        }
    }

    ngOnDestroy() {
        if (this.pageUpdate$) {
            this.pageUpdate$.unsubscribe();
        }
    }

    private initPage(id: any) {
        this.sendDocumentService.getOneSendDocumentsById(id).subscribe((data: SendDocumentModel) => {
            this.sendDocument = data;
            this.errorOneDocument = false;
        }, error => {
            this.errorOneDocumentModel = this.errorService.errorProcess(error);
            this.errorOneDocument = true;
        });
        this.sendDocumentService.getListSendDocumentBytesBySendDocumentId(id).subscribe((data: any) => {
            this.sendDocumentsBytes = data.items;
            this.errorDocumentBytes = false;
        }, error => {
            this.errorDocumentBytesModel = this.errorService.errorProcess(error);
            this.errorDocumentBytes = true;
        });
        this.sendDocumentService.getListJournalSendDocumentBySendDocumentId(id).subscribe((data: any) => {
                this.journalSendDocuments = data.items;
                this.errorJournalDocuments = false;
            }, error => {
                this.errorJournalDocumentsModel = this.errorService.errorProcess(error);
                this.errorJournalDocuments = true;
            }
        );
    }

    private initStateDetails(id: number) {
        const stateDetails = this.stateService.getFilter().detailsState;
        this.currentIds = stateDetails.currentIds;
        if (this.currentIds.length !== 0) {
            this.isNextUp = id !== this.currentIds[0];
            this.isNextDown = id !== this.currentIds[this.currentIds.length - 1];
        }
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

    isReceipt(type: string) {
        if (this.locale.getLocale() === 'da') {
            return type === 'Kvittering';
        } else {
            return type === 'Receipt';
        }
    }
}
