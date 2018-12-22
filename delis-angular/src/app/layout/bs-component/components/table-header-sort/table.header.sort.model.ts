export class TableHeaderSortModel {

    columnName: string;
    columnClick: number;

    constructor(model ?: any) {
        if (model) {
            this.columnName = model.columnName;
            this.columnClick = model.columnClick;
        }
    }
}
