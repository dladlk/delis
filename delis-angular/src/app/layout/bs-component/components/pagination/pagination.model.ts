export class PaginationModel {

    collectionSize: number;
    currentPage: number;
    pageSize: number;

    constructor() {
        this.collectionSize = 10;
        this.currentPage = 1;
        this.pageSize = 10;
    }
}
