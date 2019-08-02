import { Observable } from "rxjs";
import { AbstractEntityModel } from "../../model/content/abstract-entity.model";
import { TableStateModel } from "../../model/filter/table-state.model";

export interface DelisService<E extends AbstractEntityModel, F extends TableStateModel> {

    getAll(filter: F): Observable<any>;
}
