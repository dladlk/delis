export class PaginationModel {

    collectionSize: number;
    currentPage: number;
    pageSize: number;
    selectedPageSize: any;

    constructor() {
        this.collectionSize = 0;
        this.currentPage = 1;
        this.pageSize = 10;
        this.selectedPageSize = {pageSize: 10};
    }
}
