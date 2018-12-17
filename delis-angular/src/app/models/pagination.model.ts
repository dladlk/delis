export interface IPagination {

  pageNumber: number;
  pageSize: number;
  totalElement: number;
  totalPages: number;

}

export class PaginationModel implements IPagination {
  pageNumber: number;
  pageSize: number;
  totalElement: number;

  constructor(pagination: IPagination = {
    pageNumber: 1,
    pageSize: 1,
    totalElement: 1,
    totalPages: 1}
    ) {
    this.setPagination(pagination);
  }

  setPagination(pagination: IPagination) {
    this.pageNumber = pagination.pageNumber;
    this.pageSize = pagination.pageSize;
    this.totalElement = pagination.totalElement;
  }

  get totalPages(): number {
    return Math.ceil(this.totalElement / this.pageSize);
  }

  getPagination() {
    return this;
  }
}
