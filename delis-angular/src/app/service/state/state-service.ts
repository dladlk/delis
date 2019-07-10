import { TableStateModel } from "../../model/filter/table-state.model";

export interface StateService<T extends TableStateModel> {

    filter: T;

    getFilter(): T;

    setFilter(filter: T);
}
