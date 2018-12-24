export class PaginationModel {

    collectionSize: number;
    currentPage: number;
    pageSize: number;

    constructor() {
        this.collectionSize = 0;
        this.currentPage = 1;
        this.pageSize = 10;
    }
}
