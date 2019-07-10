import { DataSource } from "@angular/cdk/table";
import { CollectionViewer } from "@angular/cdk/collections";
import { Observable } from "rxjs";
import { TableStateModel } from "../../model/filter/table-state.model";
import { AbstractEntityModel } from "../../model/content/abstract-entity.model";

export interface DelisDataSource<T extends AbstractEntityModel, S extends TableStateModel> extends DataSource<T> {

    connect(collectionViewer: CollectionViewer): Observable<T[] | ReadonlyArray<T>>;
    disconnect(collectionViewer: CollectionViewer): void;
    getLoading(): Observable<boolean>;
    getTotalElements(): Observable<number>;
    load(filter: S);
}
