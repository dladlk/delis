import { DataSource } from '@angular/cdk/table';
import { CollectionViewer } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { IdentifierService } from '../../../service/content/identifier.service';
import { IdentifierModel } from '../../../model/content/identifier/identifier.model';
import { IdentifierFilterModel } from '../../../model/filter/identifier-filter.model';
import {DelisDataSource} from "../delis-data-source";
import {DelisService} from "../../../service/content/delis-service";
import {AbstractEntityModel} from "../../../model/content/abstract-entity.model";
import {TableStateModel} from "../../../model/filter/table-state.model";

export class IdentifierDataSource implements DelisDataSource<IdentifierModel, IdentifierFilterModel> {

  private identifierSubject = new BehaviorSubject<IdentifierModel[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private loadingTotalElements = new BehaviorSubject<number>(0);
  public loading$ = this.loadingSubject.asObservable();
  public totalElements$ = this.loadingTotalElements.asObservable();

  constructor(private identifierService: DelisService<AbstractEntityModel, TableStateModel>) {}

  connect(collectionViewer: CollectionViewer): Observable<IdentifierModel[] | ReadonlyArray<IdentifierModel>> {
    return this.identifierSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.identifierSubject.complete();
    this.loadingSubject.complete();
    this.loadingTotalElements.complete();
  }

  load(filter: IdentifierFilterModel) {
    this.loadingSubject.next(true);
    this.identifierService.getAll(filter).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false)))
      .subscribe(res => {
        this.identifierSubject.next(res.items);
        this.loadingTotalElements.next(res.collectionSize);
      });
  }
}
