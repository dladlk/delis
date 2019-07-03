import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import {DocumentModel} from '../../../model/content/document/document.model';
import {DocumentService} from '../../../service/content/document.service';
import {DocumentFilterModel} from '../../../model/filter/document-filter.model';

export class DocumentDataSource implements DataSource<DocumentModel> {

  private documentSubject = new BehaviorSubject<DocumentModel[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private loadingTotalElements = new BehaviorSubject<number>(0);
  public loading$ = this.loadingSubject.asObservable();
  public totalElements$ = this.loadingTotalElements.asObservable();

  constructor(private documentService: DocumentService) {}

  connect(collectionViewer: CollectionViewer): Observable<DocumentModel[] | ReadonlyArray<DocumentModel>> {
    return this.documentSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.documentSubject.complete();
    this.loadingSubject.complete();
    this.loadingTotalElements.complete();
  }

  load(pageIndex: number, pageSize: number, filter: DocumentFilterModel) {
    this.loadingSubject.next(true);
    this.documentService.getListDocuments(pageIndex, pageSize, filter).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false)))
      .subscribe(res => {
        this.documentSubject.next(res.items);
        this.loadingTotalElements.next(res.collectionSize);
      });
  }
}
