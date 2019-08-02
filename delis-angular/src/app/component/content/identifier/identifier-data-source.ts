import { CollectionViewer } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { IdentifierModel } from '../../../model/content/identifier/identifier.model';
import { IdentifierFilterModel } from '../../../model/filter/identifier-filter.model';
import { DelisDataSource } from "../delis-data-source";
import { DelisService } from "../../../service/content/delis-service";
import { AbstractEntityModel } from "../../../model/content/abstract-entity.model";
import { TableStateModel } from "../../../model/filter/table-state.model";
import { StateService } from "../../../service/state/state-service";

export class IdentifierDataSource implements DelisDataSource<IdentifierModel, IdentifierFilterModel> {

  private identifierSubject = new BehaviorSubject<IdentifierModel[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private loadingTotalElements = new BehaviorSubject<number>(0);

  constructor(private identifierService: DelisService<AbstractEntityModel, TableStateModel>) {}

  connect(collectionViewer: CollectionViewer): Observable<IdentifierModel[] | ReadonlyArray<IdentifierModel>> {
    return this.identifierSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.identifierSubject.complete();
    this.loadingSubject.complete();
    this.loadingTotalElements.complete();
  }

  load(stateService: StateService<IdentifierFilterModel>) {
    this.loadingSubject.next(true);
    this.identifierService.getAll(stateService.getFilter()).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false)))
      .subscribe(res => {
        this.setStateDetails(stateService, res);
        this.identifierSubject.next(res.items);
        this.loadingTotalElements.next(res.collectionSize);
      });
  }

  public getLoading(): Observable<boolean> {
    return this.loadingSubject.asObservable();
  }

  public getTotalElements(): Observable<number> {
    return this.loadingTotalElements.asObservable();
  }

  private setStateDetails(stateService: StateService<IdentifierFilterModel>, res: any) {
    if (res.items !== undefined && res.items.length !== 0) {
      let ids = res.items.map(value => value.id);
      let filter = stateService.getFilter();
      filter.detailsState.currentIds = ids;
      stateService.setFilter(filter);
    }
  }
}
