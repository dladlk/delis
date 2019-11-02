import { CollectionViewer } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { SendDocumentModel } from '../../../model/content/send-document/send-document.model';
import { SendDocumentFilterModel } from '../../../model/filter/send-document-filter.model';
import { DelisDataSource } from '../delis-data-source';
import { DelisService } from '../../../service/content/delis-service';
import { AbstractEntityModel } from '../../../model/content/abstract-entity.model';
import { TableStateModel } from '../../../model/filter/table-state.model';
import { StateService } from '../../../service/state/state-service';

export class SendDocumentDataSource implements DelisDataSource<SendDocumentModel, SendDocumentFilterModel> {

  private sendDocumentSubject = new BehaviorSubject<SendDocumentModel[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private loadingTotalElements = new BehaviorSubject<number>(0);

  constructor(private sendDocumentService: DelisService<AbstractEntityModel, TableStateModel>) {}

  connect(collectionViewer: CollectionViewer): Observable<SendDocumentModel[] | ReadonlyArray<SendDocumentModel>> {
    return this.sendDocumentSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.sendDocumentSubject.complete();
    this.loadingSubject.complete();
    this.loadingTotalElements.complete();
  }

  load(stateService: StateService<SendDocumentFilterModel>) {
    this.loadingSubject.next(true);
    this.sendDocumentService.getAll(stateService.getFilter()).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false)))
      .subscribe(res => {
        this.setStateDetails(stateService, res);
        this.sendDocumentSubject.next(res.items);
        this.loadingTotalElements.next(res.collectionSize);
      });
  }

  public getLoading(): Observable<boolean> {
    return this.loadingSubject.asObservable();
  }

  public getTotalElements(): Observable<number> {
    return this.loadingTotalElements.asObservable();
  }

  private setStateDetails(stateService: StateService<SendDocumentFilterModel>, res: any) {
    if (res.items !== undefined && res.items.length !== 0) {
      const ids = res.items.map(value => value.id);
      const filter = stateService.getFilter();
      filter.detailsState.currentIds = ids;
      stateService.setFilter(filter);
    }
  }
}
