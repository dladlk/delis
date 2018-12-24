import { PaginationModel } from "../layout/bs-component/components/pagination/pagination.model";

export class PageContainerModel<T> extends PaginationModel {

    collectionSize: number;
    currentPage: number;
    pageSize: number;
    items: T[] = [];
}
