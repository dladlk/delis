export class PageContainerModel<T> {

    collectionSize: number;
    currentPage: number;
    pageSize: number;
    items: T[] = [];

    initContainer(collectionSize: number, currentPage: number, pageSize: number, items: T[]) {
        this.collectionSize = collectionSize;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.items = items;
        return this;
    }
}
