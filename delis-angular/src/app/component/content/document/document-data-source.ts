import { CollectionViewer } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { DocumentModel } from '../../../model/content/document/document.model';
import { DocumentFilterModel } from '../../../model/filter/document-filter.model';
import { DelisDataSource } from "../delis-data-source";
import { DelisService } from "../../../service/content/delis-service";
import { AbstractEntityModel } from "../../../model/content/abstract-entity.model";
import { TableStateModel } from "../../../model/filter/table-state.model";
import { StateService } from "../../../service/state/state-service";

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

  load(stateService: StateService<DocumentFilterModel>) {
    this.loadingSubject.next(true);
    this.documentService.getAll(stateService.getFilter()).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false)))
      .subscribe(res => {
        this.setStateDetails(stateService, res);
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

  private setStateDetails(stateService: StateService<DocumentFilterModel>, res: any) {
    if (res.items !== undefined && res.items.length !== 0) {
      let ids = res.items.map(value => value.id);
      let filter = stateService.getFilter();
      filter.detailsState.currentIds = ids;
      stateService.setFilter(filter);
    }
  }
}
