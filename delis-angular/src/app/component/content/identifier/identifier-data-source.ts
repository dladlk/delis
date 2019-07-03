import { DataSource } from '@angular/cdk/table';
import { CollectionViewer } from '@angular/cdk/collections';
import { IdentifierModel } from '../../../model/content/identifier/identifier.model';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { IdentifierService } from '../../../service/content/identifier.service';
import { IdentifierFilterModel } from '../../../model/filter/identifier-filter.model';

export class IdentifierDataSource implements DataSource<IdentifierModel> {

  private identifierSubject = new BehaviorSubject<IdentifierModel[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private loadingTotalElements = new BehaviorSubject<number>(0);
  public loading$ = this.loadingSubject.asObservable();
  public totalElements$ = this.loadingTotalElements.asObservable();

  constructor(private identifierService: IdentifierService) {}

  connect(collectionViewer: CollectionViewer): Observable<IdentifierModel[] | ReadonlyArray<IdentifierModel>> {
    return this.identifierSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.identifierSubject.complete();
    this.loadingSubject.complete();
    this.loadingTotalElements.complete();
  }

  load(pageIndex: number, pageSize: number, filter: IdentifierFilterModel) {
    this.loadingSubject.next(true);
    this.identifierService.getListIdentifiers(pageIndex, pageSize, filter).pipe(
      catchError(() => of([])),
      finalize(() => this.loadingSubject.next(false)))
      .subscribe(res => {
        this.identifierSubject.next(res.items);
        this.loadingTotalElements.next(res.collectionSize);
      });
  }
}
