export interface IPagination {

    collectionSize: number;
    currentPage: number;
    previousPage: number;
    pageSize: number;

}

export class PaginationModel implements IPagination {

    collectionSize: number;
    currentPage: number;
    previousPage: number;
    pageSize: number;

    constructor(pagination: IPagination = {
        collectionSize: 10,
        currentPage: 1,
        previousPage: 1,
        pageSize: 1
    }) {
        this.setPagination(pagination);
    }

    setPagination(pagination: IPagination) {
        this.collectionSize = pagination.collectionSize;
        this.currentPage = pagination.currentPage;
        this.previousPage = pagination.previousPage;
        this.pageSize = pagination.pageSize;
    }

    getPagination() {
        return this;
    }
}
