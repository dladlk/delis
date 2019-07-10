import { CollectionViewer } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { DocumentModel } from '../../../model/content/document/document.model';
import { DocumentFilterModel } from '../../../model/filter/document-filter.model';
import { DelisDataSource } from "../delis-data-source";
import { DelisService } from "../../../service/content/delis-service";
import { AbstractEntityModel } from "../../../model/content/abstract-entity.model";
import { TableStateModel } from "../../../model/filter/table-state.model";

export class DocumentDataSource implements DelisDataSource<DocumentModel, DocumentFilterModel> {

  private documentSubject = new BehaviorSubject<DocumentModel[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private loadingTotalElements = new BehaviorSubject<number>(0);

  constructor(private documentService: DelisService<AbstractEntityModel, TableStateModel>) {}

  connect(collectionViewer: CollectionViewer): Observable<DocumentModel[] | ReadonlyArray<DocumentModel>> {
    return this.documentSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.documentSubject.complete();
    this.loadingSubject.complete();
    this.loadingTotalElements.complete();
  }

  load(filter: DocumentFilterModel) {
    this.loadingSubject.next(true);
    this.documentService.getAll(filter).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false)))
      .subscribe(res => {
        this.documentSubject.next(res.items);
        this.loadingTotalElements.next(res.collectionSize);
      });
  }

  getLoading(): Observable<boolean> {
    return this.loadingSubject.asObservable();
  }

  getTotalElements(): Observable<number> {
    return this.loadingTotalElements.asObservable();
  }
}
