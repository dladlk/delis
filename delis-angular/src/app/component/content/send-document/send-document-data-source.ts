import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { SendDocumentModel } from '../../../model/content/send-document/send-document.model';
import { SendDocumentService } from '../../../service/content/send-document.service';
import { SendDocumentFilterModel } from '../../../model/filter/send-document-filter.model';

export class SendDocumentDataSource implements DataSource<SendDocumentModel> {

  private sendDocumentSubject = new BehaviorSubject<SendDocumentModel[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private loadingTotalElements = new BehaviorSubject<number>(0);
  public loading$ = this.loadingSubject.asObservable();
  public totalElements$ = this.loadingTotalElements.asObservable();

  constructor(private sendDocumentService: SendDocumentService) {}

  connect(collectionViewer: CollectionViewer): Observable<SendDocumentModel[] | ReadonlyArray<SendDocumentModel>> {
    return this.sendDocumentSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.sendDocumentSubject.complete();
    this.loadingSubject.complete();
    this.loadingTotalElements.complete();
  }

  load(pageIndex: number, pageSize: number, filter: SendDocumentFilterModel) {
    this.loadingSubject.next(true);
    this.sendDocumentService.getListSendDocuments(pageIndex, pageSize, filter).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false)))
      .subscribe(res => {
        this.sendDocumentSubject.next(res.items);
        this.loadingTotalElements.next(res.collectionSize);
      });
  }
}
