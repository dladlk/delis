import { CollectionViewer } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { SendDocumentModel } from '../../../model/content/send-document/send-document.model';
import { SendDocumentFilterModel } from '../../../model/filter/send-document-filter.model';
import { DelisDataSource } from "../delis-data-source";
import { DelisService } from "../../../service/content/delis-service";
import { AbstractEntityModel } from "../../../model/content/abstract-entity.model";
import { TableStateModel } from "../../../model/filter/table-state.model";

export class SendDocumentDataSource implements DelisDataSource<SendDocumentModel, SendDocumentFilterModel> {

  private sendDocumentSubject = new BehaviorSubject<SendDocumentModel[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private loadingTotalElements = new BehaviorSubject<number>(0);
  public loading$ = this.loadingSubject.asObservable();
  public totalElements$ = this.loadingTotalElements.asObservable();

  constructor(private sendDocumentService: DelisService<AbstractEntityModel, TableStateModel>) {}

  connect(collectionViewer: CollectionViewer): Observable<SendDocumentModel[] | ReadonlyArray<SendDocumentModel>> {
    return this.sendDocumentSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.sendDocumentSubject.complete();
    this.loadingSubject.complete();
    this.loadingTotalElements.complete();
  }

  load(filter: SendDocumentFilterModel) {
    this.loadingSubject.next(true);
    this.sendDocumentService.getAll(filter).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false)))
      .subscribe(res => {
        this.sendDocumentSubject.next(res.items);
        this.loadingTotalElements.next(res.collectionSize);
      });
  }
}
